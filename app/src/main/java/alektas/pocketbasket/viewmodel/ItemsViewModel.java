package alektas.pocketbasket.viewmodel;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

import alektas.pocketbasket.App;
import alektas.pocketbasket.data.ItemGenerator;
import alektas.pocketbasket.data.RepositoryImpl;
import alektas.pocketbasket.domain.Repository;
import alektas.pocketbasket.domain.entities.ItemModel;
import alektas.pocketbasket.domain.usecases.AddItemUseCase;
import alektas.pocketbasket.domain.usecases.PutItemToBasket;
import alektas.pocketbasket.domain.usecases.SelectCategoryUseCase;
import alektas.pocketbasket.domain.usecases.UseCase;
import alektas.pocketbasket.guide.Guide;
import alektas.pocketbasket.guide.GuideContract;
import alektas.pocketbasket.guide.GuideImpl;

public class ItemsViewModel extends AndroidViewModel {
    private static final String TAG = "ItemsViewModel";
    private Guide mGuide;
    private Repository mRepository;
    private String mCurGuideCase;

    private MutableLiveData<Boolean> showcaseModeState = new MutableLiveData<>();
    private MutableLiveData<Boolean> guideModeState = new MutableLiveData<>();

    public ItemsViewModel(@NonNull Application application) {
        super(application);
        mRepository = RepositoryImpl.getInstance(application);
        mRepository.showcaseModeState().observe(showcaseModeState::setValue);
    }

    /**
     * Show in the Showcase only items with specified tag
     * @param tag item type or category
     */
    public void setFilter(String tag) {
        UseCase<String, Void> useCase = new SelectCategoryUseCase(mRepository);
        useCase.execute(tag, null);
    }

    /**
     * Return default showcase items
     * @param fullReset if true delete all user items
     */
    public void resetShowcase(boolean fullReset) {
        if (fullReset) mRepository.resetShowcase();
        else mRepository.insertAll(ItemGenerator.getAll());
    }

    /**
     * Update information of all vanilla items (exclude user items).
     * Information includes icons that were added in the new version and displayed names that
     * were changed with localization.
     */
    public void updateAllItems() {
        mRepository.updateAll();
    }

    public LiveData<Boolean> showcaseModeState() {
        return showcaseModeState;
    }

    public boolean isShowcaseMode() {
        if (showcaseModeState.getValue() == null) return true;
        return showcaseModeState.getValue();
    }

    public void setShowcaseMode(boolean showcaseMode) {
        mRepository.setShowcaseMode(showcaseMode);
        mGuide.onCaseHappened(GuideContract.GUIDE_CHANGE_MODE);
    }

    public List<? extends ItemModel> getBasketItems() {
        return mRepository.getBasketData().getValue();
    }

    /**
     * Create a new item in the Showcase if it doesn't already exist,
     * then add it to the Basket
     */
    public void onSearch(String name) {
        UseCase<String, Boolean> useCase = new AddItemUseCase(mRepository);
        useCase.execute(name, isAdded -> {
            if (isAdded) mGuide.onCaseHappened(GuideContract.GUIDE_ADD_ITEM);
        });
    }

    public void putToBasket(String name) {
        UseCase<String, Boolean> useCase = new PutItemToBasket(mRepository);
        useCase.execute(name, isAdded -> {
            mGuide.onCaseHappened(GuideContract.GUIDE_ADD_ITEM);
        });

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, name);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
//        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, getCategory(name));
        App.getAnalytics().logEvent(FirebaseAnalytics.Event.ADD_TO_CART, bundle);
    }

    /**
     * Verify if all items in the Basket are checked.
     */
    public void checkAllItems() {
        mRepository.checkAll();
    }

    /**
     * Delete all checked items in the Basket.
     */
    public void deleteChecked() {
        mRepository.deleteChecked();
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
        mGuide.setCase(curGuideCase); // TODO: called twice in "Guide.startFrom" method
    }

    public void disableGuideMode() {
        guideModeState.setValue(false);
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
