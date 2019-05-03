package alektas.pocketbasket.viewmodel;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import alektas.pocketbasket.App;
import alektas.pocketbasket.Utils;
import alektas.pocketbasket.data.ItemGenerator;
import alektas.pocketbasket.data.Repository;
import alektas.pocketbasket.data.RepositoryImpl;
import alektas.pocketbasket.db.entities.BasketMeta;
import alektas.pocketbasket.db.entities.Item;
import alektas.pocketbasket.guide.Guide;
import alektas.pocketbasket.guide.GuideContract;
import alektas.pocketbasket.guide.GuideImpl;

public class ItemsViewModel extends AndroidViewModel {
    private static final String TAG = "ItemsViewModel";
    private Guide mGuide;
    private LiveData<List<Item>> mShowcaseData;
    private LiveData<List<Item>> mBasketData;
    private List<Item> mDelItems;
    private Repository mRepoManager;
    private String mCurGuideCase;

    private MutableLiveData<Integer> selectedItemPosition = new MutableLiveData<>();
    private MutableLiveData<Boolean> delModeState = new MutableLiveData<>();
    private MutableLiveData<Boolean> showcaseModeState = new MutableLiveData<>();
    private MutableLiveData<Boolean> guideModeState = new MutableLiveData<>();


    public ItemsViewModel(@NonNull Application application) {
        super(application);
        mRepoManager = new RepositoryImpl(application);
        mShowcaseData = mRepoManager.getShowcaseData();
        mBasketData = mRepoManager.getBasketData();
        mDelItems = new ArrayList<>();
    }


    /* Basket methods */

    public LiveData<List<Item>> getBasketData() {
        return mBasketData;
    }

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

    public LiveData<List<Item>> getShowcaseData() {
        return mShowcaseData;
    }

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
        if (fullReset) mRepoManager.resetShowcase();
        else mRepoManager.insertAll(ItemGenerator.getAll());
    }

    /**
     * Update all items, including icons, that were added in new versions
     */
    public void updateAllItems() {
        mRepoManager.updateAll();
    }

    public boolean onItemLongClick(Item item, RecyclerView.ViewHolder holder) {
        if (!isDelMode()) {
            setDelMode(true);
        }
        prepareToDel(item, holder.getAdapterPosition());
        return true;
    }

    public void onItemClick(Item item, RecyclerView.ViewHolder holder) {
        int pos = holder.getAdapterPosition();
        if (isDelMode()) {
            if (mDelItems.contains(item)) {
                removeFromDel(item, pos);
            } else {
                prepareToDel(item, pos);
            }

        } else {
            if (getBasketMeta(item.getName()) == null) {
                putToBasket(item.getName());
            } else {
                removeFromBasket(item.getName());
            }
        }
    }

    public LiveData<Boolean> showcaseModeState() {
        return showcaseModeState;
    }

    public boolean isShowcaseMode() {
        if (showcaseModeState.getValue() == null) return true;
        return showcaseModeState.getValue();
    }

    public void setShowcaseMode(boolean showcaseMode) {
        showcaseModeState.setValue(showcaseMode);
        mGuide.onCaseHappened(GuideContract.GUIDE_CHANGE_MODE);
    }


    /* Handle delete mode */

    /**
     * Turn off the Delete Mode in which user can delete items from the Showcase
     * with deleting selected items.
     */
    public void deleteSelectedItems() {
        /* Put to argument new List to avoid ConcurrentModificationException.
         * That causes by deleting items in AsyncTask and
         * clearing this list in Main Thread at one time */
        deleteItems(new ArrayList<>(mDelItems));
        cancelDel();
    }

    /**
     * Turn off the Delete Mode in which user can delete items from the Showcase
     * without deleting selected items.
     */
    public void cancelDel() {
        setDelMode(false);
        mDelItems.clear();
    }

    private void prepareToDel(Item item, int position) {
        mDelItems.add(item);
        selectedItemPosition.setValue(position);
    }

    private void removeFromDel(Item item, int position) {
        mDelItems.remove(item);
        selectedItemPosition.setValue(position);
    }

    public LiveData<Integer> getSelectedItemPosition() {
        return selectedItemPosition;
    }

    /**
     * @return items selected by the user for removal from the Showcase
     */
    public List<Item> getDelItems() { return mDelItems; }

    public boolean isDelMode() {
        if (delModeState.getValue() == null) return false;
        return delModeState.getValue();
    }

    public LiveData<Boolean> delModeState() {
        return delModeState;
    }

    /**
     * Turn on/off the Delete Mode in which user can delete items from the Showcase
     */
    private void setDelMode(boolean delMode) {
        if (!isDelModeAllowed()) {
            delModeState.setValue(false);
            return;
        }

        if (delMode) {
            mGuide.onCaseHappened(GuideContract.GUIDE_DEL_MODE);
        } else {
            mGuide.onCaseHappened(GuideContract.GUIDE_DEL_ITEMS);
        }

        delModeState.setValue(delMode);
    }

    /**
     * When the Guide is started the Delete Mode allowed only in several cases
     */
    private boolean isDelModeAllowed() {
        if (!mGuide.isGuideStarted()) return true;
        return GuideContract.GUIDE_DEL_MODE.equals(mGuide.currentCaseKey())
                || GuideContract.GUIDE_DEL_ITEMS.equals(mGuide.currentCaseKey());
    }


    /* Guide methods */

    public void startGuide() {
        if (mGuide == null) {
            Log.e(TAG, "startGuide: to start the guide " +
                            "you need to set Guide by setGuide method",
                    new NullPointerException("GuideImpl is null"));
        }

        guideModeState.setValue(true);
        mGuide.startGuide();

        Bundle startGuide = new Bundle();
        App.getAnalytics().logEvent(FirebaseAnalytics.Event.TUTORIAL_BEGIN, startGuide);
    }

    /**
     * Finish guide during any case.
     * Warning! Do not invoke it in the {@link GuideImpl.GuideListener#onGuideFinish()}
     * to avoid the infinity loop.
     */
    public void finishGuide() {
        mGuide.finishGuide();
        guideModeState.setValue(false);

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

    public boolean isGuideMode() {
        if (guideModeState.getValue() == null) return false;
        return guideModeState.getValue();
    }

    public LiveData<Boolean> guideModeState() {
        return guideModeState;
    }

    public String getCurGuideCase() {
        return mCurGuideCase;
    }

    public void setGuideCase(String curGuideCase) {
        mCurGuideCase = curGuideCase;
        mGuide.setCase(curGuideCase);
    }

    public void setGuide(Guide guide) {
        mGuide = guide;
    }

    public Guide getGuide() {
        return mGuide;
    }

    /* Other methods */

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

}
