package alektas.pocketbasket.model;

import android.app.Application;

import alektas.pocketbasket.async.getAllAsync;
import alektas.pocketbasket.async.insertAllAsync;
import alektas.pocketbasket.db.entity.BasketMeta;
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

    @Override
    public BasketMeta getBasketMeta(String key) {
        try {
            return new getItemMetaAsync(mItemsDao).execute(key).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<BasketMeta> getBasketMeta() {
        try {
            return new getAllMetaAsync(mItemsDao).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void putToBasket(@NonNull String name) {
        new putToBasketAsync(mItemsDao, this).execute(name);
    }

    @Override
    public void updatePositions(List<Item> items) {
        new updatePositionsAsync(mItemsDao).execute(items);
    }

    // Change item state in "Basket"
    @Override
    public void checkItem(@NonNull String name) {
        new checkAsync(mItemsDao).execute(name);
    }

    public boolean isChecked(String name) {
        try {
            return (new isCheckedAsync(mItemsDao).execute(name).get()) != 0;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void checkAll(boolean state) {
        if (state) new checkAllAsync(mItemsDao).execute(1);
        else new checkAllAsync(mItemsDao).execute(0);
    }

    // Delete item from "Basket"
    @Override
    public void removeBasketItem(@NonNull String name) {
        new removeBasketItemAsync(mItemsDao, this).execute(name);
    }

    // Delete all checked items from "Basket"
    @Override
    public void deleteChecked() {
        new deleteCheckedAsync(mItemsDao, this).execute();
    }

    /* Showcase methods */

    @Override
    public void addNewItem(Item item) {
        new addNewItem(mItemsDao, this).execute(item);
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

    private static class updatePositionsAsync extends AsyncTask<List<Item>, Void, Void> {
        private ItemsDao mDao;

        updatePositionsAsync(ItemsDao dao) {
            mDao = dao;
        }

        @Override
        protected Void doInBackground(List<Item>... items) {
            mDao.updatePositions(items[0]);
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

    private static class getAllMetaAsync extends AsyncTask<Void, Void, List<BasketMeta>> {
        private ItemsDao mDao;

        getAllMetaAsync(ItemsDao dao) { mDao = dao; }

        @Override
        protected List<BasketMeta> doInBackground(Void... meta) {
            return mDao.getBasketMeta();
        }
    }

    private static class checkAllAsync extends AsyncTask<Integer, Void, Void> {
        private ItemsDao mDao;

        checkAllAsync(ItemsDao dao) { mDao = dao; }

        @Override
        protected Void doInBackground(Integer... state) {
            mDao.checkAll(state[0]);
            return null;
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

    private static class checkAsync extends AsyncTask<String, Void, Void> {
        private ItemsDao mDao;

        checkAsync(ItemsDao dao) { mDao = dao; }

        @Override
        protected final Void doInBackground(String... state) {
            mDao.check(state[0]);
            return null;
        }
    }

    private static class deleteCheckedAsync extends AsyncTask<Void, Void, Void> {
        private ItemsDao mDao;
        private Observer mObserver;

        deleteCheckedAsync(ItemsDao dao) { mDao = dao; }

        deleteCheckedAsync(ItemsDao dao, Observer observer) {
            this(dao);
            mObserver = observer;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mDao.deleteChecked();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mObserver != null) mObserver.update();
        }
    }

    private static class removeBasketItemAsync extends AsyncTask<String, Void, Void> {
        private ItemsDao mDao;
        private Observer mObserver;

        removeBasketItemAsync(ItemsDao dao) { mDao = dao; }

        removeBasketItemAsync(ItemsDao dao, Observer observer) {
            this(dao);
            mObserver = observer;
        }

        @Override
        protected final Void doInBackground(String... name) {
            mDao.deleteBasketItem(name[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mObserver != null) mObserver.update();
        }
    }

    private static class addNewItem extends AsyncTask<Item, Void, Void> {
        private ItemsDao mDao;
        private Observer mObserver;

        addNewItem(ItemsDao dao) { mDao = dao; }

        addNewItem(ItemsDao dao, Observer observer) {
            this(dao);
            mObserver = observer;
        }

        @Override
        protected final Void doInBackground(Item... items) {
            mDao.addNewItem(items[0]);
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







