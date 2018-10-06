package alektas.pocketbasket.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import alektas.pocketbasket.db.entity.Item;
import alektas.pocketbasket.model.RepoManager;

public class ItemsViewModel extends AndroidViewModel {
    private LiveData<List<Item>> mShowcaseData;
    private LiveData<List<Item>> mBasketData;
    private List<Item> mDelItems;
    private RepoManager mRepoManager;
    private boolean isDelMode = false;
    private boolean isShowcaseMode = true;
    private boolean isBasketNamesShow = false;
    private boolean isShowcaseNamesShow = true;

    public ItemsViewModel(@NonNull Application application) {
        super(application);
        mRepoManager = new RepoManager(application);
        mShowcaseData = mRepoManager.getAllItems();
        mBasketData = mRepoManager.getBasketItems();
        mDelItems = new ArrayList<>();
    }

    public List<Item> getDelItems() { return mDelItems; }

    public LiveData<List<Item>> getShowcaseData() {
        return mShowcaseData;
    }

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

    public void removeBasketItem(String key) {
        mRepoManager.removeBasketItem(key);
    }

    public void deleteItem(Item item) {
        mRepoManager.deleteItem(item);
    }

    public void deleteAll(List<Item> items) {
        for (Item item : items) {
            mRepoManager.deleteItem(item);
        }
    }

    public void clearBasket() { mRepoManager.clearBasket(); }

    public boolean isDelMode() {
        return isDelMode;
    }

    public void setDelMode(boolean delMode) {
        isDelMode = delMode;
    }

    public boolean isShowcaseMode() {
        return isShowcaseMode;
    }

    public void setShowcaseMode(boolean showcaseMode) {
        isShowcaseMode = showcaseMode;
    }

    public boolean isBasketNamesShow() {
        return isBasketNamesShow;
    }

    public void setBasketNamesShow(boolean basketNamesShow) {
        isBasketNamesShow = basketNamesShow;
    }

    public boolean isShowcaseNamesShow() {
        return isShowcaseNamesShow;
    }

    public void setShowcaseNamesShow(boolean showcaseNamesShow) {
        isShowcaseNamesShow = showcaseNamesShow;
    }

    public void checkItem(String key) {
        mRepoManager.changeItemState(key);
    }
}
