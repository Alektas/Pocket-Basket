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
import alektas.pocketbasket.domain.entities.BasketItemModel;
import alektas.pocketbasket.domain.entities.ItemModel;
import alektas.pocketbasket.domain.entities.ShowcaseItemModel;
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
    private Observable<List<ShowcaseItemModel>> mShowcaseData;

    /**
     * Data contains only items stored in the basket.
     */
    private Observable<List<BasketItemModel>> mBasketData;

    /**
     * Contains current mode state.
     * 'true' = showcase mode, 'false' = basket mode.
     */
    private Observable<Boolean> showcaseModeState;

    /**
     * Contains 'true' if the delete mode is active.
     */
    private Observable<Boolean> delModeState;

    /**
     * Items selected by the user for removal from the Showcase
     */
    private List<ShowcaseItemModel> mDelItems;

    private RepositoryImpl(Context context) {
        mItemsDao = AppDatabase.getInstance(context, this).getDao();
        mShowcaseData = new SingleObservableValue<>(getItems(mTag));
        mBasketData = new SingleObservableValue<>(getBasketItems());
        showcaseModeState = new MultiObservableValue<>(true);
        delModeState = new SingleObservableValue<>(false);
        mDelItems = new ArrayList<>();
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
        if (!delMode) {
            mDelItems.clear();
            updateShowcase();
        }
    }

    @Override
    public void selectForDeleting(ShowcaseItemModel item) {
        if (item.isRemoval()) {
            mDelItems.remove(item);
        } else {
            mDelItems.add(item);
        }

        updateShowcase();
    }

    @Override
    public void deleteSelectedItems() {
        new deleteAllAsync(mItemsDao, this).execute(convert(mDelItems));
    }

    // TODO: make without converting
    private List<Item> convert(@NonNull List<? extends ItemModel> models) {
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

    private List<BasketItemModel> getBasketItems() {
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
    public void removeMarked() {
        new deleteMarkedAsync(mItemsDao, this).execute();
    }


    /* Showcase methods */

    @Override
    public void addNewItem(String name) {
        new addNewItem(mItemsDao, this).execute(name);
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
    public void insertPredefinedItems() {
        new insertAllAsync(mItemsDao, this).execute(convert(ItemGenerator.getAll()));
    }

    @Override
    public void updateAll() {
        new updateAllAsync(mItemsDao, this).execute(ItemGenerator.getAll());
    }

    // Set value to Showcase Data to notify observers
    @Override
    public void updateShowcase() {
        List<ShowcaseItemModel> items = getItems(mTag);
        if (items != null && delModeState.getValue()) {
            for (ShowcaseItemModel delItem : mDelItems) {
                int i = items.indexOf(delItem);
                if (i >= 0) items.get(i).setRemoval(true);
            }
        }

        mShowcaseData.setValue(items);
    }

    // Set value to the Basket Data to notify observers
    @Override
    public void updateBasket() {
        mBasketData.setValue(getBasketItems());
    }


    /* Data getters */

    @Override
    public Observable<List<ShowcaseItemModel>> getShowcaseData() {
        return mShowcaseData;
    }

    @Override
    public Observable<List<BasketItemModel>> getBasketData() {
        return mBasketData;
    }

    @Override
    public ItemModel getItem(String name) {
        try {
            return new getItemAsync(mItemsDao).execute(name).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<ShowcaseItemModel> getItems(String tag) {
        try {
            return new getAllAsync(mItemsDao).execute(tag).get();
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }


    /* AsyncTasks */

    private static class getBasketItemsAsync extends AsyncTask<Void, Void, List<BasketItemModel>> {
        private ItemsDao mDao;

        getBasketItemsAsync(ItemsDao dao) { mDao = dao; }

        @Override
        protected List<BasketItemModel> doInBackground(Void... voids) {
            return new ArrayList<>(mDao.getBasketItems());
        }
    }

    private static class getItemAsync extends AsyncTask<String, Void, ItemModel> {
        ItemsDao mDao;

        getItemAsync(ItemsDao dao) {
            mDao = dao;
        }

        @Override
        protected ItemModel doInBackground(String... strings) {
            return mDao.getShowcaseItem(strings[0]);
        }
    }

    private static class getAllAsync extends AsyncTask<String, Void, List<ShowcaseItemModel>> {
        private ItemsDao mDao;

        getAllAsync(ItemsDao dao) { mDao = dao; }

        @Override
        protected List<ShowcaseItemModel> doInBackground(String... tags) {
            if (tags.length == 0
                    || tags[0] == null
                    || tags[0].equals(ResourcesUtils.getResIdName(R.string.all))) {
                return new ArrayList<>(mDao.getShowcaseItems());
            }
            else {
                return new ArrayList<>(mDao.getShowcaseItems(tags[0]));
            }
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







