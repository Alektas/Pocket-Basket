package alektas.pocketbasket.ui;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

import alektas.pocketbasket.App;
import alektas.pocketbasket.R;
import alektas.pocketbasket.data.RepositoryImpl;
import alektas.pocketbasket.domain.Repository;
import alektas.pocketbasket.domain.entities.ItemModel;
import alektas.pocketbasket.domain.usecases.AddItemUseCase;
import alektas.pocketbasket.domain.usecases.MarkAllBasketItems;
import alektas.pocketbasket.domain.usecases.RemoveMarkedItems;
import alektas.pocketbasket.domain.usecases.ResetItemsUseCase;
import alektas.pocketbasket.domain.usecases.SelectCategoryUseCase;
import alektas.pocketbasket.domain.usecases.UpdateItemsUseCase;
import alektas.pocketbasket.domain.usecases.UseCase;
import alektas.pocketbasket.guide.domain.AppState;
import alektas.pocketbasket.guide.GuideContract;
import alektas.pocketbasket.guide.GuideObserver;
import alektas.pocketbasket.guide.domain.Requirement;
import alektas.pocketbasket.guide.domain.ContextualGuide;
import alektas.pocketbasket.guide.domain.Guide;
import alektas.pocketbasket.guide.domain.GuideCase;
import alektas.pocketbasket.guide.domain.GuideCaseImpl;
import alektas.pocketbasket.utils.ResourcesUtils;

public class ActivityViewModel extends AndroidViewModel implements GuideObserver {
    private static final String TAG = "ActivityViewModel";
    private Guide mGuide;
    private Repository mRepository;

    private MutableLiveData<Boolean> showcaseModeData = new MutableLiveData<>();
    private MutableLiveData<Boolean> guideModeData = new MutableLiveData<>();
    private MutableLiveData<String> mCurGuideCaseData = new MutableLiveData<>();
    private MutableLiveData<String> mCompletedGuideCase = new MutableLiveData<>();

    public static AppState<Boolean> removeByTapInBasketModeState =
            new AppState<>(GuideContract.STATE_REMOVE_BY_TAP, false);
    public static AppState<Boolean> delModeState =
            new AppState<>(GuideContract.STATE_DEL_MODE, false);
    public static AppState<Integer> removeCountState =
            new AppState<>(GuideContract.STATE_REMOVE_COUNT, 0);
    public static AppState<Integer> markCountState =
            new AppState<>(GuideContract.STATE_MARK_COUNT, 0);
    public static AppState<Integer> basketSizeState =
            new AppState<>(GuideContract.STATE_BASKET_SIZE, 0);
    private AppState<Boolean> landscapeState =
            new AppState<>(GuideContract.STATE_LANDSCAPE, false);
    private AppState<Boolean> famShowingState =
            new AppState<>(GuideContract.STATE_FAM_SHOWING, false);
    private AppState<Boolean> showcaseModeState =
            new AppState<>(GuideContract.STATE_MODE, false);
    private AppState<Boolean> newItemAddedState =
            new AppState<>(GuideContract.STATE_ADDED_NEW_ITEM, false);

    public ActivityViewModel(@NonNull Application application) {
        super(application);
        mRepository = RepositoryImpl.getInstance(application);
        mRepository.showcaseModeData().observe((showcaseMode) -> {
            showcaseModeData.setValue(showcaseMode);
            showcaseModeState.setState(showcaseMode);
            if (showcaseMode) {
                removeByTapInBasketModeState.setState(false);
                removeCountState.setState(0);
                markCountState.setState(0);
            }
        });

        SharedPreferences guidePrefs = application.getSharedPreferences(
                ResourcesUtils.getString(R.string.GUIDE_PREFERENCES_FILE_KEY),
                Context.MODE_PRIVATE);
        mGuide = buildGuide(guidePrefs);

        SharedPreferences prefs = application.getSharedPreferences(
                ResourcesUtils.getString(R.string.PREFERENCES_FILE_KEY),
                Context.MODE_PRIVATE);
        if (prefs.getBoolean(ResourcesUtils.getString(R.string.SHOW_HINTS_KEY), false)) {
            mGuide.observe(this);
            mGuide.start();
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mRepository.showcaseModeData().clearObservers();
        mRepository = null;
        mGuide.removeObserver(this);
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
        new ResetItemsUseCase(mRepository).execute(fullReset, null);
    }

    /**
     * Update information of all vanilla items (exclude user items).
     * Information includes icons that were added in the new version and displayed names that
     * were changed with localization.
     */
    public void updateAllItems() {
        new UpdateItemsUseCase(mRepository).execute(null, null);
    }

    public LiveData<Boolean> showcaseModeState() {
        return showcaseModeData;
    }

    public boolean isShowcaseMode() {
        if (showcaseModeData.getValue() == null) return true;
        return showcaseModeData.getValue();
    }

    public void setShowcaseMode(boolean showcaseMode) {
        mRepository.setShowcaseMode(showcaseMode);
        mGuide.onUserEvent(GuideContract.GUIDE_CHANGE_MODE);
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
        useCase.execute(name, isNewItemAdded -> {
            if (isNewItemAdded) {
                newItemAddedState.setState(true);
            }
        });
    }

    /**
     * Verify if all items in the Basket are checked.
     */
    public void markAllItems() {
        new MarkAllBasketItems(mRepository).execute(null, null);
    }

    /**
     * Delete all checked items in the Basket.
     */
    public void deleteMarked() {
        new RemoveMarkedItems(mRepository).execute(null, null);
    }


    /* Guide methods */

    public void stopGuide() {
        if (mGuide.isStarted()) mGuide.finish();
    }

    public void startGuide(SharedPreferences guidePrefs) {
        if (mGuide != null) {
            if (mGuide.isStarted()) mGuide.finish();
            mGuide.removeObserver(this);
        }
        mGuide = buildGuide(guidePrefs);
        mGuide.observe(this);
        mGuide.start();
    }

    private Guide buildGuide(SharedPreferences prefs) {
        GuideCase addByTapCase = new GuideCaseImpl(GuideContract.GUIDE_ADD_ITEM_BY_TAP,
                prefs.getBoolean(GuideContract.GUIDE_ADD_ITEM_BY_TAP, false));
        GuideCase checkCase = new GuideCaseImpl(GuideContract.GUIDE_CHECK_ITEM,
                prefs.getBoolean(GuideContract.GUIDE_CHECK_ITEM, false));
        GuideCase changeModeCase = new GuideCaseImpl(GuideContract.GUIDE_CHANGE_MODE,
                prefs.getBoolean(GuideContract.GUIDE_CHANGE_MODE, false));
        GuideCase swipeRemoveCase = new GuideCaseImpl(GuideContract.GUIDE_SWIPE_REMOVE_ITEM,
                prefs.getBoolean(GuideContract.GUIDE_SWIPE_REMOVE_ITEM, false));
        GuideCase moveCase = new GuideCaseImpl(GuideContract.GUIDE_MOVE_ITEM,
                prefs.getBoolean(GuideContract.GUIDE_MOVE_ITEM, false));
        GuideCase delModeCase = new GuideCaseImpl(GuideContract.GUIDE_DEL_MODE,
                prefs.getBoolean(GuideContract.GUIDE_DEL_MODE, false));
        GuideCase delSelectedCase = new GuideCaseImpl(GuideContract.GUIDE_DEL_SELECTED_ITEMS,
                prefs.getBoolean(GuideContract.GUIDE_DEL_SELECTED_ITEMS, false));
        GuideCase famCase = new GuideCaseImpl(GuideContract.GUIDE_SHOW_FLOATING_MENU,
                prefs.getBoolean(GuideContract.GUIDE_SHOW_FLOATING_MENU, false));
        GuideCase famHelpCase = new GuideCaseImpl(GuideContract.GUIDE_FLOATING_MENU_HELP,
                prefs.getBoolean(GuideContract.GUIDE_FLOATING_MENU_HELP, false));

        return new ContextualGuide.Builder()
                .addCase(addByTapCase)
                .require(new Requirement(basketSizeState, delModeState) {
                    @Override
                    public boolean check() {
                        return !delModeState.getState() && basketSizeState.getState() == 0;
                    }
                })
                .addCase(checkCase)
                .require(new Requirement(basketSizeState) {
                    @Override
                    public boolean check() {
                        return basketSizeState.getState() > 0;
                    }
                })
                .addCase(changeModeCase)
                .require(new Requirement(landscapeState, basketSizeState) {
                    @Override
                    public boolean check() {
                        return !landscapeState.getState() && basketSizeState.getState() > 1;
                    }
                })
                .addCase(swipeRemoveCase)
                .require(new Requirement(showcaseModeState, basketSizeState,
                        removeByTapInBasketModeState, removeCountState) {
                    @Override
                    public boolean check() {
                        return !showcaseModeState.getState()
                                && basketSizeState.getState() > 0
                                && removeByTapInBasketModeState.getState()
                                && removeCountState.getState() > 1;
                    }
                })
                .addCase(moveCase)
                .require(new Requirement(showcaseModeState, basketSizeState) {
                    @Override
                    public boolean check() {
                        return basketSizeState.getState() > 3 && !showcaseModeState.getState();
                    }
                })
                .addCase(delModeCase)
                .require(new Requirement(newItemAddedState) {
                    @Override
                    public boolean check() {
                        return newItemAddedState.getState();
                    }
                })
                .addCase(delSelectedCase)
                .require(new Requirement(delModeState) {
                    @Override
                    public boolean check() {
                        return delModeState.getState();
                    }
                })
                .showAfter(delModeCase)
                .addCase(famCase)
                .require(new Requirement(showcaseModeState, basketSizeState, markCountState) {
                    @Override
                    public boolean check() {
                        return !showcaseModeState.getState()
                                && basketSizeState.getState() > 1
                                && markCountState.getState() > 1;

                    }
                })
                .addCase(famHelpCase)
                .require(new Requirement(showcaseModeState, famShowingState) {
                    @Override
                    public boolean check() {
                        return !showcaseModeState.getState() && famShowingState.getState();
                    }
                })
                .showAfter(famCase)
                .build();

    }

    public void onEventHappened(String eventKey) {
        mGuide.onUserEvent(eventKey);
    }

    /**
     * Finish guide during any case.
     * Warning! Do not invoke it in the {@link GuideObserver#onGuideFinish()}
     * to avoid the infinity loop.
     */
    public void onSkipGuideBtnClick() {
        mGuide.onUserEvent(mGuide.currentCase());
    }

    public boolean isGuideMode() {
        if (guideModeData.getValue() == null) return false;
        return guideModeData.getValue();
    }

    public LiveData<Boolean> guideModeState() {
        return guideModeData;
    }

    public LiveData<String> curGuideCaseData() {
        return mCurGuideCaseData;
    }

    public LiveData<String> completedGuideCaseData() {
        return mCompletedGuideCase;
    }


    /* Guide listener interface methods */

    @Override
    public void onGuideStart() {
        guideModeData.setValue(true);
        ContextualGuide guide = (ContextualGuide) mGuide;
        if (basketSizeState.getState() > 0) {
            guide.completeCase(GuideContract.GUIDE_ADD_ITEM_BY_TAP);
        }
        if (markCountState.getState() > 0) {
            guide.completeCase(GuideContract.GUIDE_CHECK_ITEM);
        }
        if (famShowingState.getState()) {
            guide.completeCase(GuideContract.GUIDE_SHOW_FLOATING_MENU);
            guide.completeCase(GuideContract.GUIDE_FLOATING_MENU_HELP);
        }
        if (delModeState.getState()) {
            guide.completeCase(GuideContract.GUIDE_DEL_MODE);
            guide.completeCase(GuideContract.GUIDE_DEL_SELECTED_ITEMS);
        }

        // TODO: make analytic appropriate to the context guide
        Bundle startGuide = new Bundle();
        App.getAnalytics().logEvent(FirebaseAnalytics.Event.TUTORIAL_BEGIN, startGuide);
    }

    @Override
    public void onGuideFinish() {
        mCurGuideCaseData.setValue(null);
        guideModeData.setValue(false);
        newItemAddedState.setState(false);
        removeByTapInBasketModeState.setState(false);
        markCountState.setState(0);
        removeCountState.setState(0);

        // TODO: make analytic appropriate to the context guide
        Bundle finGuide = new Bundle();
        App.getAnalytics().logEvent(FirebaseAnalytics.Event.TUTORIAL_COMPLETE, finGuide);
    }

    @Override
    public void onGuideCaseStart(String caseKey) {
        mCurGuideCaseData.setValue(caseKey);
    }

    @Override
    public void onGuideCaseComplete(String caseKey) {
        mCompletedGuideCase.setValue(caseKey);
    }


    /* Other methods */

    public void setOrientationState(boolean isLandscape) {
        landscapeState.setState(isLandscape);
    }

    public void onFloatingMenuCalled() {
        famShowingState.setState(true);
        mGuide.onUserEvent(GuideContract.GUIDE_SHOW_FLOATING_MENU);
    }

    public void onFloatingMenuHide() {
        famShowingState.setState(false);
    }

    public void onFabClick() {
        mGuide.onUserEvent(GuideContract.GUIDE_FLOATING_MENU_HELP);
    }

}