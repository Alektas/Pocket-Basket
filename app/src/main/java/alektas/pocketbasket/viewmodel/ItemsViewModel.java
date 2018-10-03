package alektas.pocketbasket.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import alektas.pocketbasket.db.entity.Item;
import alektas.pocketbasket.model.RepoManager;

public class ItemsViewModel extends AndroidViewModel {
    private LiveData<List<Item>> mShowcaseData;
    private LiveData<List<Item>> mBasketData;
    private RepoManager mRepoManager;
    private boolean isShowcaseMode = true;

    public ItemsViewModel(@NonNull Application application) {
        super(application);
        mRepoManager = new RepoManager(application);
        mShowcaseData = mRepoManager.getAllItems();
        mBasketData = mRepoManager.getBasketItems();
    }

    public LiveData<List<Item>> getShowcaseData() {
        return mShowcaseData;
    }

    public void setFilter(int tag) { mRepoManager.setFilter(tag); }

    public LiveData<List<Item>> getByTag(int tag) { return mRepoManager.getByTag(tag); }

    public LiveData<List<Item>> getBasketData() {
        return mBasketData;
    }

    public Item getBasketItem(String key) {
        return mRepoManager.getBasketItem(key);
    }

    public void putItem(Item item) {
        mRepoManager.addBasketItem(item);
    }

    public void insertItem(Item item) {
        mRepoManager.insertItem(item);
    }

    public void deleteItem(String key) {
        mRepoManager.deleteBasketItem(key);
    }

    public void clearBasket() { mRepoManager.clearBasket(); }

    public boolean isShowcaseMode() {
        return isShowcaseMode;
    }

    public void setShowcaseMode(boolean showcaseMode) {
        isShowcaseMode = showcaseMode;
    }

    public void checkItem(String key) {
        mRepoManager.changeItemState(key);
    }
}
