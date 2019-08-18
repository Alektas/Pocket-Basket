package alektas.pocketbasket.data;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import alektas.pocketbasket.R;
import alektas.pocketbasket.data.db.AppDatabase;
import alektas.pocketbasket.data.db.dao.ItemsDao;
import alektas.pocketbasket.data.db.entities.BasketMeta;
import alektas.pocketbasket.data.db.entities.Item;
import alektas.pocketbasket.domain.Repository;
import alektas.pocketbasket.domain.entities.BasketItemModel;
import alektas.pocketbasket.domain.entities.ItemModel;
import alektas.pocketbasket.domain.entities.ShowcaseItemModel;
import alektas.pocketbasket.domain.usecases.UseCase;
import alektas.pocketbasket.domain.utils.MultiObservableValue;
import alektas.pocketbasket.domain.utils.Observable;
import alektas.pocketbasket.domain.utils.SingleObservableValue;
import alektas.pocketbasket.utils.ResourcesUtils;

public class RepositoryImpl implements Repository, ItemsUpdater {
    private static final String TAG = "RepositoryImpl";
    private static Repository INSTANCE;
    private String mTag = ResourcesUtils.getResIdName(R.string.all);
    private ItemsDao mItemsDao;

    private Observable<List<ShowcaseItemModel>> mShowcaseData;
    private Observable<List<BasketItemModel>> mBasketData;
    private Observable<Boolean> showcaseModeState;
    private Observable<Boolean> delModeState;

    /**
     * Items selected by the user for removal from the Showcase
     */
    private List<ShowcaseItemModel> mDelItems;
    private Observable<Integer> mDelItemsCountData;

    private RepositoryImpl(Context context) {
        mItemsDao = AppDatabase.getInstance(context).getDao();
        mShowcaseData = new SingleObservableValue<>(getShowcaseItems(mTag));
        mBasketData = new SingleObservableValue<>(getBasketItems());
        mDelItemsCountData = new SingleObservableValue<>(0);
        showcaseModeState = new MultiObservableValue<>(true);
        delModeState = new MultiObservableValue<>(false);
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
    public Observable<Boolean> showcaseModeData() {
        return showcaseModeState;
    }

    @Override
    public void setShowcaseMode(boolean showcaseMode) {
        showcaseModeState.setValue(showcaseMode);
    }

    @Override
    public Observable<Boolean> delModeData() {
        return delModeState;
    }

    @Override
    public Observable<Integer> getDelItemsCountData() {
        return mDelItemsCountData;
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
        mDelItemsCountData.setValue(mDelItems.size());

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
     * @param key key of the item
     * @return contain item position in Basket and mark state
     */
    private BasketMeta getItemMeta(String key) {
        try {
            return new getItemMetaAsync(mItemsDao).execute(key).get();
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
    public boolean isItemInBasket(String key) {
        return getItemMeta(key) != null;
    }


    @Override
    public void putToBasket(@NonNull String key) {
        new putToBasketAsync(mItemsDao, this).execute(key);
    }

    @Override
    public void updatePositions(List<String> keys) {
        new updatePositionsAsync(mItemsDao, this).execute(keys);
    }

    // Change item state in "Basket"
    @Override
    public void markItem(@NonNull String key) {
        new markAsync(mItemsDao, this).execute(key);
    }

    // Check all items in Basket (or uncheck if already all items are checked)
    @Override
    public void markAll() {
        new markAllAsync(mItemsDao, this).execute();
    }

    // Delete item from "Basket"
    @Override
    public void removeFromBasket(@NonNull String key) {
        new removeBasketItemAsync(mItemsDao, this).execute(key);
    }

    @Override
    public void removeFromBasket(String key, UseCase.Callback<Boolean> callback) {
        if (callback == null) {
            new removeBasketItemAsync(mItemsDao, this).execute(key);
            return;
        }
        new removeBasketItemAsync(mItemsDao, this, callback).execute(key);
    }

    // Delete all checked items from "Basket"
    @Override
    public void removeMarked() {
        new removeBasketMarkedAsync(mItemsDao, this).execute();
    }

    @Override
    public void cleanBasket(UseCase.Callback<Boolean> callback) {
        new cleanBasketAsync(mItemsDao, this, callback).execute();
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
        new resetAsync(mItemsDao, this).execute();
    }

    @Override
    public void returnDeletedItems() {
        new returnDeletedItemsAsync(mItemsDao, this).execute();
    }

    @Override
    public void updateNames() {
        new updateDisplayedNamesAsync(mItemsDao, this).execute();
    }

    // Set value to Showcase Data to notify observers
    @Override
    public void updateShowcase() {
        // Get items according to the selected category (tag)
        List<ShowcaseItemModel> items = getShowcaseItems(mTag);
        // In del mode set removal state to the selected for deletion items
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
    public ItemModel getItemByName(String name) {
        try {
            return new getItemByNameAsync(mItemsDao).execute(name).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<ShowcaseItemModel> getShowcaseItems(String tag) {
        try {
            return new getShowcaseAsync(mItemsDao).execute(tag).get();
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

    private static class getItemByNameAsync extends AsyncTask<String, Void, ItemModel> {
        ItemsDao mDao;

        getItemByNameAsync(ItemsDao dao) {
            mDao = dao;
        }

        @Override
        protected ItemModel doInBackground(String... strings) {
            return mDao.getItemByName(strings[0]);
        }
    }

    private static class getShowcaseAsync extends AsyncTask<String, Void, List<ShowcaseItemModel>> {
        private ItemsDao mDao;

        getShowcaseAsync(ItemsDao dao) { mDao = dao; }

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
        private ItemsUpdater mUpdater;

        updatePositionsAsync(ItemsDao dao) {
            mDao = dao;
        }

        updatePositionsAsync(ItemsDao dao, ItemsUpdater updater) {
            mDao = dao;
            mUpdater = updater;
        }

        @SafeVarargs
        @Override
        protected final Void doInBackground(List<String>... names) {
            mDao.updatePositions(names[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mUpdater != null) {
                mUpdater.updateBasket();
            }
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

    private static class cleanBasketAsync extends AsyncTask<Void, Void, Void> {
        private ItemsDao mDao;
        private ItemsUpdater mUpdater;
        private UseCase.Callback<Boolean> mCallback;

        cleanBasketAsync(ItemsDao dao) { mDao = dao; }

        cleanBasketAsync(ItemsDao dao, ItemsUpdater updater) {
            mDao = dao;
            mUpdater = updater;
        }

        cleanBasketAsync(ItemsDao dao, ItemsUpdater updater, UseCase.Callback<Boolean> callback) {
            mDao = dao;
            mUpdater = updater;
            mCallback = callback;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mDao.cleanBasket();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mUpdater != null) {
                mUpdater.updateBasket();
                mUpdater.updateShowcase();
            }
            if (mCallback != null) mCallback.onResponse(true);
        }
    }

    private static class removeBasketMarkedAsync extends AsyncTask<Void, Void, Void> {
        private ItemsDao mDao;
        private ItemsUpdater mUpdater;

        removeBasketMarkedAsync(ItemsDao dao) { mDao = dao; }

        removeBasketMarkedAsync(ItemsDao dao, ItemsUpdater updater) {
            this(dao);
            mUpdater = updater;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mDao.removeCheckedBasket();
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
        private UseCase.Callback<Boolean> mCallback;

        removeBasketItemAsync(ItemsDao dao) { mDao = dao; }

        removeBasketItemAsync(ItemsDao dao, ItemsUpdater updater) {
            this(dao);
            mUpdater = updater;
        }

        removeBasketItemAsync(ItemsDao dao, ItemsUpdater updater, UseCase.Callback<Boolean> callback) {
            this(dao, updater);
            mCallback = callback;
        }

        @Override
        protected final Void doInBackground(String... name) {
            mDao.removeBasketItem(name[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mUpdater != null) {
                mUpdater.updateShowcase();
                mUpdater.updateBasket();
            }
            if (mCallback != null) {
                mCallback.onResponse(true);
                mCallback = null;
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
            mDao.deleteItems(items[0]);
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

    private static class resetAsync extends AsyncTask<Void, Void, Void> {
        private ItemsDao mDao;
        private ItemsUpdater mUpdater;

        resetAsync(ItemsDao dao) { mDao = dao; }

        resetAsync(ItemsDao dao, ItemsUpdater updater) {
            this(dao);
            mUpdater = updater;
        }

        @Override
        protected final Void doInBackground(Void... voids) {
            mDao.fullReset();
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

    private static class returnDeletedItemsAsync extends AsyncTask<Void, Void, Void> {
        private ItemsDao mDao;
        private ItemsUpdater mUpdater;

        returnDeletedItemsAsync(ItemsDao dao) {
            mDao = dao;
        }

        returnDeletedItemsAsync(ItemsDao dao, ItemsUpdater updater) {
            this(dao);
            mUpdater = updater;
        }

        @Override
        protected final Void doInBackground(Void... voids) {
            mDao.returnDeletedItems();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mUpdater != null) mUpdater.updateShowcase();
        }
    }

    private static class updateDisplayedNamesAsync extends AsyncTask<Void, Void, Void> {
        private ItemsDao mDao;
        private ItemsUpdater mUpdater;

        updateDisplayedNamesAsync(ItemsDao dao) {
            mDao = dao;
        }

        updateDisplayedNamesAsync(ItemsDao dao, ItemsUpdater updater) {
            this(dao);
            mUpdater = updater;
        }

        @Override
        protected final Void doInBackground(Void... voids) {
            List<Item> items = new ArrayList<>(mDao.getShowcaseItems());
            for (Item item : items) {
                if (item.getNameRes() == null) continue;
                item.setName(ResourcesUtils.getString(item.getNameRes()));
            }

            mDao.update(items);
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







