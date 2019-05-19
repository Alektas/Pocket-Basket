package alektas.pocketbasket.data;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import alektas.pocketbasket.R;
import alektas.pocketbasket.async.insertAllAsync;
import alektas.pocketbasket.async.updateAllAsync;
import alektas.pocketbasket.data.db.AppDatabase;
import alektas.pocketbasket.data.db.dao.ItemsDao;
import alektas.pocketbasket.data.db.entities.BasketMeta;
import alektas.pocketbasket.data.db.entities.Item;
import alektas.pocketbasket.domain.Repository;
import alektas.pocketbasket.domain.entities.ItemModel;
import alektas.pocketbasket.domain.utils.MultiObservableValue;
import alektas.pocketbasket.domain.utils.Observable;
import alektas.pocketbasket.domain.utils.SingleObservableValue;
import alektas.pocketbasket.utils.ResourcesUtils;

public class RepositoryImpl implements Repository, ItemsUpdater {
    private static final String TAG = "RepositoryImpl";
    private static Repository INSTANCE;
    private String mTag = ResourcesUtils.getResIdName(R.string.all);
    private ItemsDao mItemsDao;

    /**
     * Data contains only items consisted to the selected category.
     */
    private Observable<List<? extends ItemModel>> mShowcaseData;

    /**
     * Data contains only items stored in the basket.
     */
    private Observable<List<? extends ItemModel>> mBasketData;

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

    private List<Item> convert(List<? extends ItemModel> models) {
        List<Item> list = new ArrayList<>();
        for (ItemModel model : models) {
            list.add((Item) model);
        }
        return list;
    }


    /* Basket methods */

    /**
     * @param name key of the item
     * @return contain item position in Basket and mark state
     */
    private BasketMeta getItemMeta(String name) {
        try {
            return new getItemMetaAsync(mItemsDao).execute(name).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<? extends ItemModel> getBasketItems() {
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
    public void markItem(@NonNull String name) {
        new markAsync(mItemsDao, this).execute(name);
    }

    public boolean isMarked(String name) {
        try {
            return (new isMarkedAsync(mItemsDao).execute(name).get()) != 0;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Check all items in Basket (or uncheck if already all items are checked)
    @Override
    public void markAll() {
        new markAllAsync(mItemsDao, this).execute();
    }

    // Delete item from "Basket"
    @Override
    public void removeFromBasket(@NonNull String name) {
        new removeBasketItemAsync(mItemsDao, this).execute(name);
    }

    // Delete all checked items from "Basket"
    @Override
    public void deleteMarked() {
        new deleteMarkedAsync(mItemsDao, this).execute();
    }


    /* Showcase methods */

    @Override
    public void addNewItem(String name) {
        new addNewItem(mItemsDao, this).execute(name);
    }

    @Override
    public void deleteItems(List<? extends ItemModel> items) {
        new deleteAllAsync(mItemsDao, this).execute(convert(items));
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
    public void insertAll(List<? extends ItemModel> items) {
        new insertAllAsync(mItemsDao, this).execute(convert(items));
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
    public Observable<List<? extends ItemModel>> getShowcaseData() {
        return mShowcaseData;
    }

    @Override
    public Observable<List<? extends ItemModel>> getBasketData() {
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

    private static class getBasketItemsAsync extends AsyncTask<Void, Void, List<Item>> {
        private ItemsDao mDao;

        getBasketItemsAsync(ItemsDao dao) { mDao = dao; }

        @Override
        protected List<Item> doInBackground(Void... voids) {
            return mDao.getBasketItems();
        }
    }

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

    private static class getAllAsync extends AsyncTask<String, Void, List<Item>> {
        private ItemsDao mDao;

        getAllAsync(ItemsDao dao) { mDao = dao; }

        @Override
        protected List<Item> doInBackground(String... tags) {
            if (tags.length == 0
                    || tags[0] == null
                    || tags[0].equals(ResourcesUtils.getResIdName(R.string.all))) return mDao.getItems();
            else return mDao.getByTag(tags[0]);
        }
    }

    private static class updatePositionsAsync extends AsyncTask<List<String>, Void, Void> {
        private ItemsDao mDao;

        updatePositionsAsync(ItemsDao dao) {
            mDao = dao;
        }

        @SafeVarargs
        @Override
        protected final Void doInBackground(List<String>... names) {
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

    private static class markAsync extends AsyncTask<String, Void, Void> {
        private ItemsDao mDao;
        private ItemsUpdater mUpdater;

        markAsync(ItemsDao dao) { mDao = dao; }

        markAsync(ItemsDao dao, ItemsUpdater updater) {
            this(dao);
            mUpdater = updater;
        }

        @Override
        protected final Void doInBackground(String... state) {
            mDao.mark(state[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mUpdater != null) {
                mUpdater.updateBasket();
            }
        }
    }

    private static class markAllAsync extends AsyncTask<Void, Void, Void> {
        private ItemsDao mDao;
        private ItemsUpdater mUpdater;

        markAllAsync(ItemsDao dao) { mDao = dao; }

        markAllAsync(ItemsDao dao, ItemsUpdater updater) {
            mDao = dao;
            mUpdater = updater;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mDao.markAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mUpdater != null) {
                mUpdater.updateBasket();
            }
        }
    }

    private static class isMarkedAsync extends AsyncTask<String, Void, Integer> {
        private ItemsDao mDao;

        isMarkedAsync(ItemsDao dao) { mDao = dao; }

        @Override
        protected Integer doInBackground(String... name) {
            return mDao.isMarked(name[0]);
        }
    }

    private static class deleteMarkedAsync extends AsyncTask<Void, Void, Void> {
        private ItemsDao mDao;
        private ItemsUpdater mUpdater;

        deleteMarkedAsync(ItemsDao dao) { mDao = dao; }

        deleteMarkedAsync(ItemsDao dao, ItemsUpdater updater) {
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







