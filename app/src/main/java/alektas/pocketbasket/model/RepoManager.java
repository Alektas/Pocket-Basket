package alektas.pocketbasket.model;

import alektas.pocketbasket.R;
import alektas.pocketbasket.Utils;
import alektas.pocketbasket.async.getAllAsync;
import alektas.pocketbasket.async.getBasketItemsAsync;
import alektas.pocketbasket.async.insertAllAsync;
import alektas.pocketbasket.db.entity.BasketMeta;
import androidx.lifecycle.LiveData;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import alektas.pocketbasket.db.AppDatabase;
import alektas.pocketbasket.db.dao.ItemsDao;
import alektas.pocketbasket.db.entity.Item;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class RepoManager implements Repository, Observer {
    private static final String TAG = "RepoManager";
    private String mTag = Utils.getIdName(R.string.all);
    private ItemsDao mItemsDao;
    private MutableLiveData<List<Item>> mShowcaseItems;
    private LiveData<List<Item>> mBasketItems;

    public RepoManager(Context context) {
        mShowcaseItems = new MutableLiveData<>();
        mItemsDao = AppDatabase.getInstance(context, this).getDao();
        update();
        mBasketItems = mItemsDao.getBasketData();
    }


    /* Basket methods */

    @Override
    public BasketMeta getItemMeta(String name) {
        try {
            return new getItemMetaAsync(mItemsDao).execute(name).get();
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

    // Check all items in Basket (or uncheck if already all items are checked)
    @Override
    public void checkAll() {
        new checkAllAsync(mItemsDao).execute();
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
    public void addNewItem(Item item) {
        new addNewItem(mItemsDao, this).execute(item);
    }

    @Override
    public void deleteItems(List<Item> items) {
        new deleteAllAsync(mItemsDao, this).execute(items);
    }

    // Show in Showcase only items with specified tag
    @Override
    public void setFilter(String tag) {
        mTag = tag;
        update();
    }

    // Return default showcase items
    @Override
    public void resetShowcase(boolean fullReset) {
        if (fullReset) {
            new resetAsync(mItemsDao, this).execute(ItemGenerator.getAll());
        } else {
            new insertAllAsync(mItemsDao, this).execute(ItemGenerator.getAll());
        }
    }


    // Set value to Showcase LiveData to notify observers
    @Override
    public void update() {
        mShowcaseItems.setValue(getItems(mTag));
    }


    /* Data getters */

    @Override
    public LiveData<List<Item>> getShowcaseData() {
        return mShowcaseItems;
    }

    @Override
    public LiveData<List<Item>> getBasketData() {
        return mBasketItems;
    }

    @Override
    public List<Item> getBasketItems() {
        try {
            return new getBasketItemsAsync(mItemsDao).execute().get();
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Item> getItems(String tag) {
        try {
            return new getAllAsync(mItemsDao).execute(tag).get();
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Item> getItems() {
        try {
            return new getAllAsync(mItemsDao).execute().get();
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

    private static class checkAllAsync extends AsyncTask<Void, Void, Void> {
        private ItemsDao mDao;

        checkAllAsync(ItemsDao dao) { mDao = dao; }

        @Override
        protected Void doInBackground(Void... voids) {
            mDao.checkAll();
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







