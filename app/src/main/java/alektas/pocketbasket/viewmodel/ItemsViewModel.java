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
        mShowcaseData = mRepoManager.getAllData();
        mBasketData = mRepoManager.getBasketData();
        mDelItems = new ArrayList<>();
    }

    /* Basket methods */

    public Item getBasketItem(String key) {
        return mRepoManager.getBasketItem(key);
    }

    public void putItemToBasket(Item item) {
        mRepoManager.addBasketItem(item);
    }

    public void checkItem(Item item) {
        mRepoManager.changeItemState(item);
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

    public void removeBasketItem(Item item) {
        mRepoManager.removeBasketItem(item);
    }

    public void clearBasket() { mRepoManager.clearBasket(); }

    /* Showcase methods */

    public void addNewItem(String name, int tagRes) {
        if (getBasketItem(name) == null) {
            Item item = new Item(name);
            item.setTagRes(tagRes);
            item.setInBasket(true);
            mRepoManager.insertItem(item);
        }
    }

    public void deleteItems(List<Item> items) {
        mRepoManager.deleteItems(items);
    }

    // Show in Showcase only items with specified tag
    public void setFilter(int tag) {
        mRepoManager.setFilter(tag);
    }

    // Return default showcase items
    public void resetShowcase() {

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
    private boolean isAllChecked() {
        if (mBasketData.getValue() == null) return false;
        for(Item item : mBasketData.getValue()) {
            if (!item.isChecked()) return false;
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
