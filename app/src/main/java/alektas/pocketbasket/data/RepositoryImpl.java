package alektas.pocketbasket.data;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutionException;

import alektas.pocketbasket.R;
import alektas.pocketbasket.Utils;
import alektas.pocketbasket.async.getAllAsync;
import alektas.pocketbasket.async.getBasketItemsAsync;
import alektas.pocketbasket.async.insertAllAsync;
import alektas.pocketbasket.async.updateAllAsync;
import alektas.pocketbasket.db.AppDatabase;
import alektas.pocketbasket.db.dao.ItemsDao;
import alektas.pocketbasket.db.entities.BasketMeta;
import alektas.pocketbasket.db.entities.Item;
import alektas.pocketbasket.domain.Repository;
import alektas.pocketbasket.domain.utils.MultiObservableValue;
import alektas.pocketbasket.domain.utils.Observable;
import alektas.pocketbasket.domain.utils.SingleObservableValue;

public class RepositoryImpl implements Repository, ItemsUpdater {
    private static final String TAG = "RepositoryImpl";
    private static Repository INSTANCE;
    private String mTag = Utils.getResIdName(R.string.all);
    private ItemsDao mItemsDao;

    /**
     * Data contains only items consisted to the selected category.
     */
    private Observable<List<Item>> mShowcaseData;

    /**
     * Data contains only items stored in the basket.
     */
    private Observable<List<Item>> mBasketData;

    /**
     * Contains current mode state.
     * 'true' = showcase mode, 'false' = basket mode.
     */
    private Observable<Boolean> showcaseModeState;

    /**
     * Contains 'true' if the delete mode is active.
     */
    private Observable<Boolean> delModeState;

    private RepositoryImpl(Context context) {
        mItemsDao = AppDatabase.getInstance(context, this).getDao();
        mShowcaseData = new SingleObservableValue<>(getItems(mTag));
        mBasketData = new SingleObservableValue<>(getBasketItems());
        showcaseModeState = new MultiObservableValue<>(true);
        delModeState = new SingleObservableValue<>(false);
    }

    public static Repository getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (RepositoryImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RepositoryImpl(context);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public Observable<Boolean> showcaseModeState() {
        return showcaseModeState;
    }

    @Override
    public void setShowcaseMode(boolean showcaseMode) {
        showcaseModeState.setValue(showcaseMode);
    }

    @Override
    public Observable<Boolean> delModeState() {
        return delModeState;
    }

    @Override
    public void setDelMode(boolean delMode) {
        delModeState.setValue(delMode);
    }

    /* Basket methods */

    /**
     * @param name key of the item
     * @return contain item position in Basket and check state
     */
    private BasketMeta getItemMeta(String name) {
        try {
            return new getItemMetaAsync(mItemsDao).execute(name).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Item> getBasketItems() {
        try {
            return new getBasketItemsAsync(mItemsDao).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isItemInBasket(String name) {
        return getItemMeta(name) != null;
    }


    @Override
    public void putToBasket(@NonNull String name) {
        new putToBasketAsync(mItemsDao, this).execute(name);
    }

    @Override
    public void updatePositions(List<String> names) {
        new updatePositionsAsync(mItemsDao).execute(names);
    }

    // Change item state in "Basket"
    @Override
    public void checkItem(@NonNull String name) {
        new checkAsync(mItemsDao, this).execute(name);
    }

    public boolean isChecked(String name) {
        try {
            return (new isCheckedAsync(mItemsDao).execute(name).get()) != 0;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Check all items in Basket (or uncheck if already all items are checked)
    @Override
    public void checkAll() {
        new checkAllAsync(mItemsDao, this).execute();
    }

    // Delete item from "Basket"
    @Override
    public void removeFromBasket(@NonNull String name) {
        new removeBasketItemAsync(mItemsDao, this).execute(name);
    }

    // Delete all checked items from "Basket"
    @Override
    public void deleteChecked() {
        new deleteCheckedAsync(mItemsDao, this).execute();
    }


    /* Showcase methods */

    @Override
    public void addNewItem(String name) {
        new addNewItem(mItemsDao, this).execute(name);
    }

    @Override
    public void deleteItems(List<Item> items) {
        new deleteAllAsync(mItemsDao, this).execute(items);
    }

    // Show in Showcase only items with specified tag
    @Override
    public void setFilter(String tag) {
        mTag = tag;
        updateShowcase();
    }

    // Return default showcase items
    @Override
    public void resetShowcase() {
        new resetAsync(mItemsDao, this).execute(ItemGenerator.getAll());
    }

    @Override
    public void insertAll(List<Item> items) {
        new insertAllAsync(mItemsDao, this).execute(items);
    }

    @Override
    public void updateAll() {
        new updateAllAsync(mItemsDao, this).execute(ItemGenerator.getAll());
    }

    // Set value to Showcase LiveData to notify observers
    @Override
    public void updateShowcase() {
        mShowcaseData.setValue(getItems(mTag));
    }

    @Override
    public void updateBasket() {
        mBasketData.setValue(getBasketItems());
    }


    /* Data getters */

    @Override
    public Observable<List<Item>> getShowcaseData() {
        return mShowcaseData;
    }

    @Override
    public Observable<List<Item>> getBasketData() {
        return mBasketData;
    }

    @Override
    public Item getItem(String name) {
        try {
            return new getItemAsync(mItemsDao).execute(name).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Item> getItems(String tag) {
        try {
            return new getAllAsync(mItemsDao).execute(tag).get();
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }


    /* AsyncTasks */

    private static class getItemAsync extends AsyncTask<String, Void, Item> {
        ItemsDao mDao;

        getItemAsync(ItemsDao dao) {
            mDao = dao;
        }

        @Override
        protected Item doInBackground(String... strings) {
            return mDao.getItem(strings[0]);
        }
    }

    private static class updatePositionsAsync extends AsyncTask<List<String>, Void, Void> {
        private ItemsDao mDao;

        updatePositionsAsync(ItemsDao dao) {
            mDao = dao;
        }

        @Override
        protected Void doInBackground(List<String>... names) {
            mDao.updatePositions(names[0]);
            return null;
        }
    }

    private static class getItemMetaAsync extends AsyncTask<String, Void, BasketMeta> {
        private ItemsDao mDao;

        getItemMetaAsync(ItemsDao dao) { mDao = dao; }

        @Override
        protected BasketMeta doInBackground(String... meta) {
            return mDao.getItemMeta(meta[0]);
        }
    }

    private static class putToBasketAsync extends AsyncTask<String, Void, Void> {
        private ItemsDao mDao;
        private ItemsUpdater mUpdater;

        putToBasketAsync(ItemsDao dao) { mDao = dao; }

        putToBasketAsync(ItemsDao dao, ItemsUpdater updater) {
            this(dao);
            mUpdater = updater;
        }

        @Override
        protected final Void doInBackground(String... names) {
            mDao.putItemToBasket(names[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mUpdater != null) {
                mUpdater.updateShowcase();
                mUpdater.updateBasket();
            }
        }
    }

    private static class checkAsync extends AsyncTask<String, Void, Void> {
        private ItemsDao mDao;
        private ItemsUpdater mUpdater;

        checkAsync(ItemsDao dao) { mDao = dao; }

        checkAsync(ItemsDao dao, ItemsUpdater updater) {
            this(dao);
            mUpdater = updater;
        }

        @Override
        protected final Void doInBackground(String... state) {
            mDao.check(state[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mUpdater != null) {
                mUpdater.updateBasket();
            }
        }
    }

    private static class checkAllAsync extends AsyncTask<Void, Void, Void> {
        private ItemsDao mDao;
        private ItemsUpdater mUpdater;

        checkAllAsync(ItemsDao dao) { mDao = dao; }

        checkAllAsync(ItemsDao dao, ItemsUpdater updater) {
            mDao = dao;
            mUpdater = updater;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mDao.checkAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mUpdater != null) {
                mUpdater.updateBasket();
            }
        }
    }

    private static class isCheckedAsync extends AsyncTask<String, Void, Integer> {
        private ItemsDao mDao;

        isCheckedAsync(ItemsDao dao) { mDao = dao; }

        @Override
        protected Integer doInBackground(String... name) {
            return mDao.isChecked(name[0]);
        }
    }

    private static class deleteCheckedAsync extends AsyncTask<Void, Void, Void> {
        private ItemsDao mDao;
        private ItemsUpdater mUpdater;

        deleteCheckedAsync(ItemsDao dao) { mDao = dao; }

        deleteCheckedAsync(ItemsDao dao, ItemsUpdater updater) {
            this(dao);
            mUpdater = updater;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mDao.deleteChecked();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mUpdater != null) {
                mUpdater.updateShowcase();
                mUpdater.updateBasket();
            }
        }
    }

    private static class removeBasketItemAsync extends AsyncTask<String, Void, Void> {
        private ItemsDao mDao;
        private ItemsUpdater mUpdater;

        removeBasketItemAsync(ItemsDao dao) { mDao = dao; }

        removeBasketItemAsync(ItemsDao dao, ItemsUpdater updater) {
            this(dao);
            mUpdater = updater;
        }

        @Override
        protected final Void doInBackground(String... name) {
            mDao.deleteBasketItem(name[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mUpdater != null) {
                mUpdater.updateShowcase();
                mUpdater.updateBasket();
            }
        }
    }

    private static class addNewItem extends AsyncTask<String, Void, Void> {
        private ItemsDao mDao;
        private ItemsUpdater mUpdater;

        addNewItem(ItemsDao dao) { mDao = dao; }

        addNewItem(ItemsDao dao, ItemsUpdater updater) {
            this(dao);
            mUpdater = updater;
        }

        @Override
        protected final Void doInBackground(String... name) {
            mDao.addNewItem(name[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mUpdater != null) {
                mUpdater.updateShowcase();
                mUpdater.updateBasket();
            }
        }
    }

    private static class deleteAllAsync extends AsyncTask<List<Item>, Void, Void> {
        private ItemsDao mDao;
        private ItemsUpdater mUpdater;

        deleteAllAsync(ItemsDao dao) { mDao = dao; }

        deleteAllAsync(ItemsDao dao, ItemsUpdater updater) {
            this(dao);
            mUpdater = updater;
        }

        @SafeVarargs
        @Override
        protected final Void doInBackground(List<Item>... items) {
            mDao.delete(items[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mUpdater != null) {
                mUpdater.updateShowcase();
                mUpdater.updateBasket();
            }
        }
    }

    private static class resetAsync extends AsyncTask<List<Item>, Void, Void> {
        private ItemsDao mDao;
        private ItemsUpdater mUpdater;

        resetAsync(ItemsDao dao) { mDao = dao; }

        resetAsync(ItemsDao dao, ItemsUpdater updater) {
            this(dao);
            mUpdater = updater;
        }

        @SafeVarargs
        @Override
        protected final Void doInBackground(List<Item>... items) {
            mDao.fullReset(items[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mUpdater != null) {
                mUpdater.updateShowcase();
                mUpdater.updateBasket();
            }
        }
    }
}







