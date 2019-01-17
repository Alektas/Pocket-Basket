package alektas.pocketbasket.viewmodel;

import android.app.Application;
import android.util.Log;

import alektas.pocketbasket.db.entity.BasketMeta;
import alektas.pocketbasket.guide.GuideHelperImpl;
import alektas.pocketbasket.model.Repository;
import alektas.pocketbasket.guide.GuideHelper;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import alektas.pocketbasket.db.entity.Item;
import alektas.pocketbasket.model.RepoManager;

public class ItemsViewModel extends AndroidViewModel {
    private static final String TAG = "ItemsViewModel";
    private GuideHelper mGuide;
    private LiveData<List<Item>> mShowcaseData;
    private LiveData<List<Item>> mBasketData;
    private List<Item> mDelItems;
    private Repository mRepoManager;
    private boolean isDelMode = false;
    private boolean isShowcaseMode = true;
    private boolean isGuideStarted = false;

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
        completeAddItemGuideCase();
    }

    public void updatePositions(List<Item> items) {
        mRepoManager.updatePositions(items);
        if (isGuideStarted
                && GuideHelperImpl.GUIDE_MOVE_ITEM.equals(mGuide.currentCase())) {
            mGuide.nextCase();
        }
    }

    public boolean isInBasket(Item item) {
        return getBasketMeta(item.getName()) != null;
    }

    public void checkItem(String name) {
        mRepoManager.checkItem(name);
        if (isGuideStarted
                && GuideHelperImpl.GUIDE_CHECK_ITEM.equals(mGuide.currentCase())) {
            mGuide.nextCase();
        }
    }

    public boolean isChecked(String name) {
        return mRepoManager.isChecked(name);
    }

    public void checkAll() {
        mRepoManager.checkAll();
    }

    public void removeFromBasket(String name) {
        mRepoManager.removeFromBasket(name);
        if (isGuideStarted
                && GuideHelperImpl.GUIDE_REMOVE_ITEM.equals(mGuide.currentCase())) {
            mGuide.nextCase();
        }
    }

    public void deleteChecked() {
        mRepoManager.deleteChecked();
    }


    /* Showcase methods */

    public void addNewItem(String name, String tagRes) {
        if (name == null) return;
        for (Item item : mRepoManager.getItems()) {
            if ( (name.toLowerCase())
                    .equals(item.getName().toLowerCase()) ) {
                putToBasket(item.getName());
                return;
            }
        }
        Item item = new Item(name);
        item.setTagRes(tagRes);
        mRepoManager.addNewItem(item);

        completeAddItemGuideCase();
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

    // Return true if delete mode allowed
    public boolean setDelMode(boolean delMode) {
        if (isGuideStarted) {
            if (delMode && GuideHelperImpl.GUIDE_DEL_MODE.equals(mGuide.currentCase())) {
                isDelMode = true;
                mGuide.nextCase();
            } else if (!delMode && GuideHelperImpl.GUIDE_DEL_ITEMS.equals(mGuide.currentCase())) {
                isDelMode = false;
                mGuide.nextCase();
            } else {
                isDelMode = false;
                return false;
            }
        }
        isDelMode = delMode;
        return true;
    }

    public boolean isShowcaseMode() {
        return isShowcaseMode;
    }

    public void setShowcaseMode(boolean showcaseMode) {
        isShowcaseMode = showcaseMode;
        if (isGuideStarted
                && GuideHelperImpl.GUIDE_CHANGE_MODE.equals(mGuide.currentCase())) {
            mGuide.nextCase();
        }
    }

    public void setGuide(GuideHelper guide) {
        mGuide = guide;
    }

    public void setGuideStarted(boolean guideStarted) {
        isGuideStarted = guideStarted;
    }

    public boolean isGuideStarted() {
        return isGuideStarted;
    }

    public boolean isModeChangedAllowed() {
        if (isGuideStarted) {
            return GuideHelperImpl.GUIDE_CHANGE_MODE.equals(mGuide.currentCase());
        } else {
            return true;
        }
    }

    public boolean isTouchAllowed() {
        return  !(isGuideStarted
                && (GuideHelperImpl.GUIDE_CATEGORIES_HELP.equals(mGuide.currentCase())
                || GuideHelperImpl.GUIDE_SHOWCASE_HELP.equals(mGuide.currentCase())
                || GuideHelperImpl.GUIDE_BASKET_HELP.equals(mGuide.currentCase())) );
    }

    public void onFloatingMenuShown() {
        if (isGuideStarted
                && GuideHelperImpl.GUIDE_FLOATING_MENU.equals(mGuide.currentCase())) {
            mGuide.nextCase();
        }
    }

    public void onFabClick() {
        if (isGuideStarted
                && GuideHelperImpl.GUIDE_FLOATING_MENU_HELP.equals(mGuide.currentCase())) {
            mGuide.nextCase();
        }
    }


    /* Guide methods */

    public void startGuide() {
        if (mGuide == null) {
            Log.e(TAG, "startGuide: to start the guide " +
                            "you need to set GuideHelper by setGuide method",
                    new NullPointerException("Guide is null"));
        }

        isGuideStarted = true;
        mGuide.startGuide();
    }

    public void finishGuide() {
        mGuide.finishGuide();
    }

    public void onSkipGuideBtnClick() {
        finishGuide();
    }

    public void nextGuideCase() {
        mGuide.nextCase();
    }

    private void completeAddItemGuideCase() {
        if (isGuideStarted
                && GuideHelperImpl.GUIDE_ADD_ITEM.equals(mGuide.currentCase())) {
            mGuide.nextCase();
        }
    }
}
