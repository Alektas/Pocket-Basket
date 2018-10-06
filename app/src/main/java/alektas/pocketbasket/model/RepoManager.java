package alektas.pocketbasket.model;

import android.app.Application;
import androidx.lifecycle.LiveData;
import android.os.AsyncTask;
import androidx.annotation.NonNull;

import alektas.pocketbasket.db.AppDatabase;
import alektas.pocketbasket.db.dao.ItemsDao;
import alektas.pocketbasket.db.entity.Item;

import java.util.List;

public class RepoManager implements Model {
    private static final String TAG = "RepoManager";
    private ItemsDao mItemsDao;
    private LiveData<List<Item>> mShowcaseItems;
    private LiveData<List<Item>> mBasketItems;

    public RepoManager(Application application) {
        mItemsDao = AppDatabase.getInstance(application).getDao();
        mShowcaseItems = mItemsDao.getAll();
        mBasketItems = mItemsDao.getBasketItems();
    }

    // Add item to "Basket".
    @Override
    public void addBasketItem(@NonNull Item item) {
        item.setInBasket(true);
        new updateAsync(mItemsDao).execute(item);
    }

    // Change item state in "Basket"
    @Override
    public void changeItemState(String key) {
        Item item = getBasketItem(key);
        if (item != null) {
            item.setChecked(!item.isChecked());
            new updateAsync(mItemsDao).execute(item);
        }
    }

    // Delete item from "Basket"
    @Override
    public void removeBasketItem(String key) {
        Item item = getBasketItem(key);
        if (item != null) {
            item.setInBasket(false);
            item.setChecked(false);
            new updateAsync(mItemsDao).execute(item);
        }
    }

    @Override
    public void deleteItem(Item item) {
        new deleteAsync(mItemsDao).execute(item);
    }

    // Delete all items from "Basket"
    @Override
    public void clearBasket() {
        new clearAsync(mItemsDao).execute();
    }

    // Return item from "Basket"
    @Override
    public Item getBasketItem(String key) {
        List<Item> items = mBasketItems.getValue();
        if (items == null) return null;
        for (Item item : items) {
            if (item.getName().equals(key)
                    && item.isInBasket()) {
                return item;
            }
        }
        return null;
    }

    @Override
    public void insertItem(Item item) {
        new insertAsync(mItemsDao).execute(item);
    }

    @Override
    public void setFilter(int tag) {
        if (tag == 0) mShowcaseItems = mItemsDao.getAll();
        else mShowcaseItems = getByTag(tag);
    }

    // Return list of items from "Showcase"
    @Override
    public LiveData<List<Item>> getAllItems() {
        return mShowcaseItems;
    }

    @Override
    public LiveData<List<Item>> getByTag(int tag) {
        if (tag == 0) return mItemsDao.getAll();
        return mItemsDao.getByTag(tag);
    }

    // Return list of items from "Basket"
    @Override
    public LiveData<List<Item>> getBasketItems() {
        return mBasketItems;
    }

    private static class updateAsync extends AsyncTask<Item, Void, Void> {
        private ItemsDao mDao;

        updateAsync(ItemsDao dao) { mDao = dao; }

        @Override
        protected Void doInBackground(Item... items) {
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

    private static class deleteAsync extends AsyncTask<Item, Void, Void> {
        private ItemsDao mDao;

        deleteAsync(ItemsDao dao) { mDao = dao; }

        @Override
        protected final Void doInBackground(Item... items) {
            mDao.delete(items[0]);
            return null;
        }
    }
}
