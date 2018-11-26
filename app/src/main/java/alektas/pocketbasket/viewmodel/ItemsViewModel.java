package alektas.pocketbasket.viewmodel;

import android.app.Application;
import android.util.Log;

import alektas.pocketbasket.db.entity.BasketMeta;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import alektas.pocketbasket.db.entity.Item;
import alektas.pocketbasket.model.RepoManager;

public class ItemsViewModel extends AndroidViewModel {
    private static final String TAG = "ItemsViewModel";
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
        mShowcaseData = mRepoManager.getAllData();
        mBasketData = mRepoManager.getBasketData();
        mDelItems = new ArrayList<>();
    }

    /* Basket methods */

    public List<BasketMeta> getBasketMeta() {
        return mRepoManager.getBasketMeta();
    }

    public BasketMeta getBasketMeta(String key) {
        return mRepoManager.getBasketMeta(key);
    }

    public void putToBasket(String name) {
        mRepoManager.putToBasket(name);
    }

    public void updatePositions(List<Item> items) {
        mRepoManager.updatePositions(items);
    }

    public boolean isInBasket(Item item) {
        return getBasketMeta(item.getName()) != null;
    }

    public void checkItem(String name) {
        mRepoManager.checkItem(name);
    }

    public boolean isChecked(String name) {
        return mRepoManager.isChecked(name);
    }

    // Check all items in Basket (or uncheck if already all items are checked)
    public void checkAll() {
        if (isAllChecked()) {
            mRepoManager.checkAll(false);
        }
        else {
            mRepoManager.checkAll(true);
        }
    }

    public void removeFromBasket(String name) {
        mRepoManager.removeBasketItem(name);
    }

    public void deleteChecked() {
        mRepoManager.deleteChecked();
    }

    /* Showcase methods */

    public void addNewItem(String name, int tagRes) {
        if (name == null) { return; }
        for (Item item : mRepoManager.getItems(0)) {
            if ( (name.toLowerCase())
                    .equals(item.getName().toLowerCase()) ) {
                mRepoManager.putToBasket(item.getName());
                return;
            }
        }
        Item item = new Item(name);
        item.setTagRes(tagRes);
        mRepoManager.addNewItem(item);
    }

    public void deleteItems(List<Item> items) {
        mRepoManager.deleteItems(items);
    }

    // Show in Showcase only items with specified tag
    public void setFilter(int tag) {
        mRepoManager.setFilter(tag);
    }

    // Return default showcase items
    public void resetShowcase(boolean fullReset) {
        mRepoManager.resetShowcase(fullReset);
    }

    /* Data getters */

    public LiveData<List<Item>> getShowcaseData() {
        return mShowcaseData;
    }

    public LiveData<List<Item>> getBasketData() {
        return mBasketData;
    }

    public List<Item> getDelItems() { return mDelItems; }

    /* Private */

    // Return 'true' if all items in Basket are checked
    private boolean isAllChecked() { // TODO: replace by query
        if (mBasketData.getValue() == null) return false;
        for(Item item : mBasketData.getValue()) {
            if (!mRepoManager.isChecked(item.getName())) return false;
        }
        return true;
    }

    /* Application state methods */

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
}
