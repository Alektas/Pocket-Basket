package alektas.pocketbasket.viewmodel;

import android.app.Application;

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

    public ItemsViewModel(@NonNull Application application) {
        super(application);
        mRepoManager = new RepoManager(application);
        mShowcaseData = mRepoManager.getShowcaseData();
        mBasketData = mRepoManager.getBasketData();
        mDelItems = new ArrayList<>();
    }


    /* Basket methods */

    public BasketMeta getBasketMeta(String key) {
        return mRepoManager.getItemMeta(key);
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

    public void checkAll() {
        mRepoManager.checkAll();
    }

    public void removeFromBasket(String name) {
        mRepoManager.removeFromBasket(name);
    }

    public void deleteChecked() {
        mRepoManager.deleteChecked();
    }


    /* Showcase methods */

    public void addNewItem(String name, String tagRes) {
        if (name == null) { return; }
        for (Item item : mRepoManager.getItems()) {
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
    public void setFilter(String tag) {
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
}
