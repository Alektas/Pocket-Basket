package alektas.pocketbasket.model;

import android.app.Application;

import alektas.pocketbasket.async.getAllAsync;
import alektas.pocketbasket.async.insertAllAsync;
import alektas.pocketbasket.async.searchAsync;
import androidx.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import alektas.pocketbasket.db.AppDatabase;
import alektas.pocketbasket.db.dao.ItemsDao;
import alektas.pocketbasket.db.entity.Item;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class RepoManager implements Model, Observer {
    private static final String TAG = "RepoManager";
    private int mTag = 0;
    private ItemsDao mItemsDao;
    private MutableLiveData<List<Item>> mShowcaseItems;
    private LiveData<List<Item>> mBasketItems;

    public RepoManager(Application application) {
        mShowcaseItems = new MutableLiveData<>();
        mItemsDao = AppDatabase.getInstance(application, this).getDao();
        update();
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
    public void putToBasket(@NonNull Item item) {
        new putToBasketAsync(mItemsDao, this).execute(item.getName());
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
        update();
    }

    // Delete all items from "Basket"
    @Override
    public void clearBasket() {
        new clearAsync(mItemsDao, this).execute();
    }

    /* Showcase methods */

    @Override
    public void insertItem(Item item) {
        new insertAsync(mItemsDao, this).execute(item);
    }

    @Override
    public void deleteItems(List<Item> items) {
        new deleteAllAsync(mItemsDao, this).execute(items);
    }

    @Override
    public void setFilter(int tag) {
        mTag = tag;
        update();
    }

    @Override
    public void resetShowcase(boolean fullReset) {
        if (fullReset) {
            new resetAsync(mItemsDao, this).execute(ItemGenerator.getAll());
        } else {
            new insertAllAsync(mItemsDao, this).execute(ItemGenerator.getAll());
        }
    }

    @Override
    public void update() {
        mShowcaseItems.setValue(getItems(mTag));
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

    @Override
    public List<Item> getItems(int tag) {
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

    private static class putToBasketAsync extends AsyncTask<String, Void, Void> {
        private ItemsDao mDao;
        private Observer mObserver;

        putToBasketAsync(ItemsDao dao) { mDao = dao; }

        putToBasketAsync(ItemsDao dao, Observer observer) {
            this(dao);
            mObserver = observer;
        }

        @Override
        protected final Void doInBackground(String... names) {
            mDao.putItemToBasket(names[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mObserver != null) mObserver.update();
        }
    }

    private static class updateAllAsync extends AsyncTask<List<Item>, Void, Void> {
        private ItemsDao mDao;

        updateAllAsync(ItemsDao dao) { mDao = dao; }

        @SafeVarargs
        @Override
        protected final Void doInBackground(List<Item>... items) {
            mDao.update(items[0]);
            return null;
        }
    }

    private static class clearAsync extends AsyncTask<Void, Void, Void> {
        private ItemsDao mDao;
        private Observer mObserver;

        clearAsync(ItemsDao dao) { mDao = dao; }

        clearAsync(ItemsDao dao, Observer observer) {
            this(dao);
            mObserver = observer;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mDao.clearBasket();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mObserver != null) mObserver.update();
        }
    }

    private static class insertAsync extends AsyncTask<Item, Void, Void> {
        private ItemsDao mDao;
        private Observer mObserver;

        insertAsync(ItemsDao dao) { mDao = dao; }

        insertAsync(ItemsDao dao, Observer observer) {
            this(dao);
            mObserver = observer;
        }

        @Override
        protected final Void doInBackground(Item... items) {
            mDao.insert(items[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mObserver != null) mObserver.update();
        }
    }

    private static class deleteAllAsync extends AsyncTask<List<Item>, Void, Void> {
        private ItemsDao mDao;
        private Observer mObserver;

        deleteAllAsync(ItemsDao dao) { mDao = dao; }

        deleteAllAsync(ItemsDao dao, Observer observer) {
            this(dao);
            mObserver = observer;
        }

        @SafeVarargs
        @Override
        protected final Void doInBackground(List<Item>... items) {
            mDao.delete(items[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mObserver != null) mObserver.update();
        }
    }

    private static class resetAsync extends AsyncTask<List<Item>, Void, Void> {
        private ItemsDao mDao;
        private Observer mObserver;

        resetAsync(ItemsDao dao) { mDao = dao; }

        resetAsync(ItemsDao dao, Observer observer) {
            this(dao);
            mObserver = observer;
        }

        @SafeVarargs
        @Override
        protected final Void doInBackground(List<Item>... items) {
            mDao.fullReset(items[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mObserver != null) mObserver.update();
        }
    }
}







