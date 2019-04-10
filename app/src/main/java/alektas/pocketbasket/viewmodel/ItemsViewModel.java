package alektas.pocketbasket.viewmodel;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

import alektas.pocketbasket.App;
import alektas.pocketbasket.Utils;
import alektas.pocketbasket.data.RepositoryImpl;
import alektas.pocketbasket.db.entities.BasketMeta;
import alektas.pocketbasket.guide.Guide;
import alektas.pocketbasket.guide.GuideContract;
import alektas.pocketbasket.data.Repository;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import alektas.pocketbasket.db.entities.Item;

public class ItemsViewModel extends AndroidViewModel {
    private static final String TAG = "ItemsViewModel";
    private Guide mGuide;
    private LiveData<List<Item>> mShowcaseData;
    private LiveData<List<Item>> mBasketData;
    private List<Item> mDelItems;
    private Repository mRepoManager;
    private boolean isDelMode = false;
    private boolean isShowcaseMode = true;
    private boolean isGuideMode = false;
    private String mCurGuideCase;

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
        mGuide.onCaseHappened(GuideContract.GUIDE_ADD_ITEM);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, name);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, getCategory(name));
        App.getAnalytics().logEvent(FirebaseAnalytics.Event.ADD_TO_CART, bundle);
    }

    private String getCategory(String itemName) {
        Item item = getItem(itemName);
        if (item == null) return "null";
        return Utils.getString(item.getTagRes());
    }

    /**
     * Caused by dragging items in the Basket.
     * Save items' positions in the Basket like a positions in the list.
     * @param items all basket items
     */
    public void updatePositions(List<Item> items) {
        mRepoManager.updatePositions(items);
        mGuide.onCaseHappened(GuideContract.GUIDE_MOVE_ITEM);
    }

    public boolean isItemInBasket(Item item) {
        return getBasketMeta(item.getName()) != null;
    }

    /**
     * Check or uncheck item in the Basket
     */
    public void checkItem(String name) {
        mRepoManager.checkItem(name);
        mGuide.onCaseHappened(GuideContract.GUIDE_CHECK_ITEM);
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
        mGuide.onCaseHappened(GuideContract.GUIDE_REMOVE_ITEM);
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
            mGuide.onCaseHappened(GuideContract.GUIDE_ADD_ITEM);
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
            mGuide.onCaseHappened(GuideContract.GUIDE_DEL_MODE);
        } else {
            mGuide.onCaseHappened(GuideContract.GUIDE_DEL_ITEMS);
        }

        isDelMode = delMode;
        return true;
    }

    public boolean isShowcaseMode() {
        return isShowcaseMode;
    }

    public void setShowcaseMode(boolean showcaseMode) {
        isShowcaseMode = showcaseMode;
        mGuide.onCaseHappened(GuideContract.GUIDE_CHANGE_MODE);
    }

    public boolean isGuideMode() {
        return isGuideMode;
    }

    public void setGuideStarted(boolean guideMode) {
        isGuideMode = guideMode;
    }

    public String getCurGuideCase() {
        return mCurGuideCase;
    }

    public void setGuideCase(String curGuideCase) {
        mCurGuideCase = curGuideCase;
        mGuide.setCase(curGuideCase);
    }

    /**
     * Continue guide from the last guide case.
     * Invoked when the phone has been rotated.
     */
    public void continueGuide() {
        mGuide.startFrom(mCurGuideCase);
    }

    public void setGuide(Guide guide) {
        mGuide = guide;
    }

    public Guide getGuide() {
        return mGuide;
    }

    /**
     * When the Guide is started the Delete Mode allowed only in several cases
     */
    private boolean isDelModeAllowed() {
        if (!mGuide.isGuideStarted()) return true;
        return GuideContract.GUIDE_DEL_MODE.equals(mGuide.currentCaseKey())
                || GuideContract.GUIDE_DEL_ITEMS.equals(mGuide.currentCaseKey());
    }

    /**
     * When the Guide is started touch allowed only in several cases
     */
    public boolean isTouchAllowed() {
        return !( mGuide.isGuideStarted() &&
                (GuideContract.GUIDE_CATEGORIES_HELP.equals(mGuide.currentCaseKey())
                || GuideContract.GUIDE_SHOWCASE_HELP.equals(mGuide.currentCaseKey())
                || GuideContract.GUIDE_BASKET_HELP.equals(mGuide.currentCaseKey())) );
    }

    public void onFloatingMenuShown() {
        mGuide.onCaseHappened(GuideContract.GUIDE_FLOATING_MENU);
    }

    public void onFabClick() {
        mGuide.onCaseHappened(GuideContract.GUIDE_FLOATING_MENU_HELP);
    }


    /* Guide methods */

    public void startGuide() {
        if (mGuide == null) {
            Log.e(TAG, "startGuide: to start the guide " +
                            "you need to set Guide by setGuide method",
                    new NullPointerException("GuideImpl is null"));
        }

        isGuideMode = true;
        mGuide.startGuide();

        Bundle startGuide = new Bundle();
        App.getAnalytics().logEvent(FirebaseAnalytics.Event.TUTORIAL_BEGIN, startGuide);
    }

    public void finishGuide() {
        mGuide.finishGuide();
        isGuideMode = false;

        Bundle finGuide = new Bundle();
        finGuide.putString(FirebaseAnalytics.Param.LEVEL_NAME,
                "finishGuide guide at case: " + mGuide.currentCaseKey());
        App.getAnalytics().logEvent(FirebaseAnalytics.Event.TUTORIAL_COMPLETE, finGuide);
    }

    public void onSkipGuideBtnClick() {
        finishGuide();
    }

    public void nextGuideCase() {
        mGuide.nextCase();
    }

}
