package alektas.pocketbasket.model;

import android.app.Application;
import androidx.lifecycle.LiveData;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import alektas.pocketbasket.db.AppDatabase;
import alektas.pocketbasket.db.dao.ItemsDao;
import alektas.pocketbasket.db.entity.Item;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class RepoManager implements Model {
    private static final String TAG = "RepoManager";
    private int mTag;
    private ItemsDao mItemsDao;
    private MutableLiveData<List<Item>> mShowcaseItems;
    private LiveData<List<Item>> mBasketItems;

    public RepoManager(Application application) {
        mItemsDao = AppDatabase.getInstance(application).getDao();
        mShowcaseItems = new MutableLiveData<>();
        mShowcaseItems.setValue(getItems(0));
        mBasketItems = mItemsDao.getBasketData();
    }

    /* Basket methods */

    // Return item from "Basket"
    @Override
    public Item getBasketItem(String key) {
        List<Item> items = mBasketItems.getValue();
        if (items == null) { return null; }
        for (Item item : items) {
            if (item.getName().equals(key)
                    && item.isInBasket()) {
                return item;
            }
        }
        return null;
    }

    // Add item to "Basket".
    @Override
    public void addBasketItem(@NonNull Item item) {
        item.setInBasket(true);
        new updateAsync(mItemsDao).execute(item);
        mShowcaseItems.setValue(getItems(mTag));
    }

    // Change item state in "Basket"
    @Override
    public void changeItemState(@NonNull Item item) {
        item.setChecked(!item.isChecked());
        new updateAsync(mItemsDao).execute(item);
    }

    @Override
    public void checkAll(boolean state) {
        List<Item> items = mBasketItems.getValue();
        for (Item item :
                items) {
            item.setChecked(state);
        }
        new updateAllAsync(mItemsDao).execute(items);
    }

    // Delete item from "Basket"
    @Override
    public void removeBasketItem(@NonNull Item item) {
        item.setInBasket(false);
        item.setChecked(false);
        new updateAsync(mItemsDao).execute(item);
        mShowcaseItems.setValue(getItems(mTag));
    }

    // Delete all items from "Basket"
    @Override
    public void clearBasket() {
        new clearAsync(mItemsDao).execute();
        mShowcaseItems.setValue(getItems(mTag));
    }

    /* Showcase methods */

    @Override
    public void insertItem(Item item) {
        new insertAsync(mItemsDao).execute(item);
        mShowcaseItems.setValue(getItems(mTag));
    }

    @Override
    public void deleteItems(List<Item> items) {
        new deleteAllAsync(mItemsDao).execute(items);
        mShowcaseItems.setValue(getItems(mTag));
    }

    @Override
    public void setFilter(int tag) {
        mTag = tag;
        mShowcaseItems.setValue(getItems(tag));
    }

    /* Data getters */

    // Return list of items from "Showcase"
    @Override
    public LiveData<List<Item>> getAllData() {
        return mShowcaseItems;
    }

    // Return list of items from "Basket"
    @Override
    public LiveData<List<Item>> getBasketData() {
        return mBasketItems;
    }

    /* Private */

    private List<Item> getItems(int tag) {
        try {
            return new getAllAsync(mItemsDao).execute(tag).get();
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    /* AsyncTasks */

    private static class updateAsync extends AsyncTask<Item, Void, Void> {
        private ItemsDao mDao;

        updateAsync(ItemsDao dao) { mDao = dao; }

        @Override
        protected Void doInBackground(Item... items) {
            mDao.update(items[0]);
            return null;
        }
    }

    private static class updateAllAsync extends AsyncTask<List<Item>, Void, Void> {
        private ItemsDao mDao;

        updateAllAsync(ItemsDao dao) { mDao = dao; }

        @Override
        protected Void doInBackground(List<Item>... items) {
            mDao.update(items[0]);
            return null;
        }
    }

    private static class clearAsync extends AsyncTask<Void, Void, Void> {
        private ItemsDao mDao;

        clearAsync(ItemsDao dao) { mDao = dao; }

        @Override
        protected Void doInBackground(Void... voids) {
            mDao.clearBasket();
            return null;
        }
    }

    private static class insertAsync extends AsyncTask<Item, Void, Void> {
        private ItemsDao mDao;

        insertAsync(ItemsDao dao) { mDao = dao; }

        @Override
        protected final Void doInBackground(Item... items) {
            mDao.insert(items[0]);
            return null;
        }
    }

    private static class deleteAllAsync extends AsyncTask<List<Item>, Void, Void> {
        private ItemsDao mDao;

        deleteAllAsync(ItemsDao dao) { mDao = dao; }

        @Override
        protected final Void doInBackground(List<Item>... items) {
            mDao.delete(items[0]);
            return null;
        }
    }

    private static class getAllAsync extends AsyncTask<Integer, Void, List<Item>> {
        private ItemsDao mDao;

        getAllAsync(ItemsDao dao) { mDao = dao; }

        @Override
        protected List<Item> doInBackground(Integer... tags) {
            if (tags[0] == 0) return mDao.getItems();
            else return mDao.getByTag(tags[0]);
        }
    }
}
