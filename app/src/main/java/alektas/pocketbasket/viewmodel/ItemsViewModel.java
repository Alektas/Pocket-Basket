package alektas.pocketbasket.viewmodel;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

import alektas.pocketbasket.App;
import alektas.pocketbasket.data.RepositoryImpl;
import alektas.pocketbasket.db.entities.BasketMeta;
import alektas.pocketbasket.guide.GuideHelperImpl;
import alektas.pocketbasket.data.Repository;
import alektas.pocketbasket.guide.GuideHelper;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import alektas.pocketbasket.db.entities.Item;

public class ItemsViewModel extends AndroidViewModel {
    private static final String TAG = "ItemsViewModel";
    private GuideHelper mGuide;
    private LiveData<List<Item>> mShowcaseData;
    private LiveData<List<Item>> mBasketData;
    private List<Item> mDelItems;
    private Repository mRepoManager;
    private boolean isDelMode = false;
    private boolean isShowcaseMode = true;

    public ItemsViewModel(@NonNull Application application) {
        super(application);
        mRepoManager = new RepositoryImpl(application);
        mShowcaseData = mRepoManager.getShowcaseData();
        mBasketData = mRepoManager.getBasketData();
        mDelItems = new ArrayList<>();
    }

    /* Basket methods */

    /**
     * @param key name of the item
     * @return contain item position in Basket and check state
     */
    public BasketMeta getBasketMeta(String key) {
        return mRepoManager.getItemMeta(key);
    }

    public void putToBasket(String name) {
        mRepoManager.putToBasket(name);
        mGuide.onCaseHappened(GuideHelperImpl.GUIDE_ADD_ITEM);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        App.getAnalytics().logEvent(FirebaseAnalytics.Event.ADD_TO_CART, bundle);
    }

    /**
     * Caused by dragging items in the Basket.
     * Save items' positions in the Basket like a positions in the list.
     * @param items all basket items
     */
    public void updatePositions(List<Item> items) {
        mRepoManager.updatePositions(items);
        mGuide.onCaseHappened(GuideHelperImpl.GUIDE_MOVE_ITEM);
    }

    public boolean isItemInBasket(Item item) {
        return getBasketMeta(item.getName()) != null;
    }

    /**
     * Check or uncheck item in the Basket
     */
    public void checkItem(String name) {
        mRepoManager.checkItem(name);
        mGuide.onCaseHappened(GuideHelperImpl.GUIDE_CHECK_ITEM);
    }

    /**
     * Verify if item in the Basket is checked.
     */
    public boolean isItemChecked(String name) {
        return mRepoManager.isChecked(name);
    }

    /**
     * Verify if all items in the Basket are checked.
     */
    public void checkAllItems() {
        mRepoManager.checkAll();
    }

    public void removeFromBasket(String name) {
        mRepoManager.removeFromBasket(name);
        mGuide.onCaseHappened(GuideHelperImpl.GUIDE_REMOVE_ITEM);
    }

    /**
     * Delete all checked items in the Basket.
     */
    public void deleteChecked() {
        mRepoManager.deleteChecked();
    }


    /* Showcase methods */

    /**
     * Create a new item in the Showcase if it doesn't already exist,
     * then add it to the Basket
     */
    public void addItem(String name) {
        if (name == null) return;

        if (getItem(name) != null) putToBasket(name);
        else {
            mRepoManager.addNewItem(new Item(name));
            mGuide.onCaseHappened(GuideHelperImpl.GUIDE_ADD_ITEM);
        }
    }

    /**
     * Find item in the Showcase by name regardless of register
     */
    private Item getItem(String name) {
        for (Item item : mRepoManager.getItems()) {
            if ( (name.toLowerCase())
                    .equals(item.getName().toLowerCase()) ) {
                return item;
            }
        }
        return null;
    }

    /**
     * Delete from the Showcase all items presented in list
     * @param items deleting items
     */
    public void deleteItems(List<Item> items) {
        mRepoManager.deleteItems(items);
    }

    /**
     * Show in the Showcase only items with specified tag
     * @param tag item type or category
     */
    public void setFilter(String tag) {
        mRepoManager.setFilter(tag);
    }

    /**
     * Return default showcase items
     * @param fullReset if true delete all user items
     */
    public void resetShowcase(boolean fullReset) {
        mRepoManager.resetShowcase(fullReset);
    }

    /**
     * Update all items, including icons, that were added in new versions
     */
    public void updateAllItems() {
        mRepoManager.updateAll();
    }


    /* Data getters */

    public LiveData<List<Item>> getShowcaseData() {
        return mShowcaseData;
    }

    public LiveData<List<Item>> getBasketData() {
        return mBasketData;
    }

    /**
     * @return items selected by the user for removal from the Showcase
     */
    public List<Item> getDelItems() { return mDelItems; }


    /* Application state methods */

    public boolean isDelMode() {
        return isDelMode;
    }

    /**
     * Turn on/off the Delete Mode in which user can delete items from the Showcase
     * @return true if delete mode allowed and was applied
     */
    public boolean setDelMode(boolean delMode) {
        if (!isDelModeAllowed()) {
            isDelMode = false;
            return false;
        }

        if (delMode) {
            mGuide.onCaseHappened(GuideHelperImpl.GUIDE_DEL_MODE);
        } else {
            mGuide.onCaseHappened(GuideHelperImpl.GUIDE_DEL_ITEMS);
        }

        isDelMode = delMode;
        return true;
    }

    public boolean isShowcaseMode() {
        return isShowcaseMode;
    }

    public void setShowcaseMode(boolean showcaseMode) {
        isShowcaseMode = showcaseMode;
        mGuide.onCaseHappened(GuideHelperImpl.GUIDE_CHANGE_MODE);
    }

    public void setGuide(GuideHelper guide) {
        mGuide = guide;
    }

    /**
     * When the Guide is started the Delete Mode allowed only in several cases
     */
    private boolean isDelModeAllowed() {
        if (!mGuide.isGuideStarted()) return true;
        return GuideHelperImpl.GUIDE_DEL_MODE.equals(mGuide.currentCase())
                || GuideHelperImpl.GUIDE_DEL_ITEMS.equals(mGuide.currentCase());
    }

    /**
     * When the Guide is started change mode allowed only in several cases
     */
    public boolean isModeChangedAllowed() {
        if (!mGuide.isGuideStarted()) return true;
        return GuideHelperImpl.GUIDE_CHANGE_MODE.equals(mGuide.currentCase());
    }

    /**
     * When the Guide is started touch allowed only in several cases
     */
    public boolean isTouchAllowed() {
        return !( mGuide.isGuideStarted() &&
                (GuideHelperImpl.GUIDE_CATEGORIES_HELP.equals(mGuide.currentCase())
                || GuideHelperImpl.GUIDE_SHOWCASE_HELP.equals(mGuide.currentCase())
                || GuideHelperImpl.GUIDE_BASKET_HELP.equals(mGuide.currentCase())) );
    }

    public void onFloatingMenuShown() {
        mGuide.onCaseHappened(GuideHelperImpl.GUIDE_FLOATING_MENU);
    }

    public void onFabClick() {
        mGuide.onCaseHappened(GuideHelperImpl.GUIDE_FLOATING_MENU_HELP);
    }


    /* Guide methods */

    public void startGuide() {
        if (mGuide == null) {
            Log.e(TAG, "startGuide: to start the guide " +
                            "you need to set GuideHelper by setGuide method",
                    new NullPointerException("Guide is null"));
        }

        mGuide.startGuide();

        Bundle startGuide = new Bundle();
        App.getAnalytics().logEvent(FirebaseAnalytics.Event.TUTORIAL_BEGIN, startGuide);
    }

    public void finishGuide() {
        mGuide.finishGuide();

        Bundle finGuide = new Bundle();
        finGuide.putString(FirebaseAnalytics.Param.LEVEL_NAME,
                "finish guide at case: " + mGuide.currentCase());
        App.getAnalytics().logEvent(FirebaseAnalytics.Event.TUTORIAL_COMPLETE, finGuide);
    }

    public void onSkipGuideBtnClick() {
        finishGuide();
    }

    public void nextGuideCase() {
        mGuide.nextCase();
    }

}
