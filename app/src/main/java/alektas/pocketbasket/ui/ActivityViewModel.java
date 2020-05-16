package alektas.pocketbasket.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import alektas.pocketbasket.App;
import alektas.pocketbasket.data.AppPreferences;
import alektas.pocketbasket.data.db.entities.BasketItem;
import alektas.pocketbasket.domain.entities.ItemModel;
import alektas.pocketbasket.domain.usecases.AddItem;
import alektas.pocketbasket.domain.usecases.UseCase;
import alektas.pocketbasket.guide.GuideContract;
import alektas.pocketbasket.guide.GuideObserver;
import alektas.pocketbasket.guide.domain.AppState;
import alektas.pocketbasket.guide.domain.ContextualGuide;
import alektas.pocketbasket.guide.domain.Guide;
import alektas.pocketbasket.guide.domain.GuideCase;
import alektas.pocketbasket.guide.domain.GuideCaseImpl;
import alektas.pocketbasket.guide.domain.Requirement;
import alektas.pocketbasket.ui.utils.LiveEvent;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static alektas.pocketbasket.di.StorageModule.GUIDE_PREFERENCES_NAME;
import static alektas.pocketbasket.di.UseCasesModule.GET_VIEW_MODE;
import static alektas.pocketbasket.di.UseCasesModule.SET_VIEW_MODE;
import static alektas.pocketbasket.di.basket.BasketUseCasesModule.GET_BASKET;
import static alektas.pocketbasket.di.basket.BasketUseCasesModule.REMOVE_CHECKED_BASKET_ITEMS;
import static alektas.pocketbasket.di.basket.BasketUseCasesModule.TOGGLE_BASKET_CHECK;
import static alektas.pocketbasket.di.showcase.ShowcaseUseCasesModule.ADD_ITEM;
import static alektas.pocketbasket.di.showcase.ShowcaseUseCasesModule.DELETE_SELECTED_SHOWCASE_ITEMS;
import static alektas.pocketbasket.di.showcase.ShowcaseUseCasesModule.GET_DEL_MODE;
import static alektas.pocketbasket.di.showcase.ShowcaseUseCasesModule.GET_SELECTED_SHOWCASE_ITEM_COUNT;
import static alektas.pocketbasket.di.showcase.ShowcaseUseCasesModule.RESET_SHOWCASE;
import static alektas.pocketbasket.di.showcase.ShowcaseUseCasesModule.SELECT_CATEGORY;
import static alektas.pocketbasket.di.showcase.ShowcaseUseCasesModule.SET_DEL_MODE;
import static alektas.pocketbasket.di.showcase.ShowcaseUseCasesModule.UPDATE_ITEMS;

public class ActivityViewModel extends ViewModel implements GuideObserver {
    private static final String TAG = "ActivityViewModel";
    private Guide mGuide;
    private UseCase<String, Single<Integer>> mAddItemUseCase;
    private UseCase<Boolean, Void> mDelModeUseCase;
    private UseCase<Void, Void> mToggleBasketCheck;
    private UseCase<Void, Completable> mRemoveMarkedBasketItemsUseCase;
    private UseCase<Boolean, Completable> mResetItemsUseCase;
    private UseCase<String, Void> mSelectCategoryUseCase;
    private UseCase<Void, Void> mUpdateItemsUseCase;
    private UseCase<Void, Completable> mDeleteSelectedShowcaseItemsUseCase;
    private UseCase<Void, Observable<List<BasketItem>>> mGetBasketItemsUseCase;
    private UseCase<Boolean, Void> mSetViewModeUseCase;

    private CompositeDisposable mDisposable = new CompositeDisposable();

    private MutableLiveData<Boolean> viewModeData = new MutableLiveData<>();
    private MutableLiveData<Boolean> deleteModeData = new MutableLiveData<>();
    private MutableLiveData<Integer> deleteItemsCountData = new MutableLiveData<>();
    private MutableLiveData<String> mCurGuideCaseData = new MutableLiveData<>();
    private MutableLiveData<String> mCompletedGuideCase = new MutableLiveData<>();
    private LiveEvent<Boolean> deleteCheckedEvent = new LiveEvent<>();
    private LiveEvent<Boolean> resetShowcaseEvent = new LiveEvent<>();

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
    private AppState<Boolean> showcaseModeState =
            new AppState<>(GuideContract.STATE_MODE, false);
    private AppState<Boolean> newItemAddedState =
            new AppState<>(GuideContract.STATE_ADDED_NEW_ITEM, false);

    @Inject
    ActivityViewModel(
            AppPreferences appPrefs,
            @Named(GUIDE_PREFERENCES_NAME) SharedPreferences guidePrefs,
            @Named(ADD_ITEM) UseCase<String, Single<Integer>> addItemUseCase,
            @Named(SET_DEL_MODE) UseCase<Boolean, Void> delModeUseCase,
            @Named(TOGGLE_BASKET_CHECK) UseCase<Void, Void> toggleBasketCheck,
            @Named(REMOVE_CHECKED_BASKET_ITEMS) UseCase<Void, Completable> removeMarkedBasketItemsUseCase,
            @Named(RESET_SHOWCASE) UseCase<Boolean, Completable> resetItemsUseCase,
            @Named(SELECT_CATEGORY) UseCase<String, Void> selectCategoryUseCase,
            @Named(UPDATE_ITEMS) UseCase<Void, Void> updateItemsUseCase,
            @Named(GET_VIEW_MODE) UseCase<Void, Observable<Boolean>> getViewModeUseCase,
            @Named(GET_DEL_MODE) UseCase<Void, Observable<Boolean>> getDelModeUseCase,
            @Named(GET_SELECTED_SHOWCASE_ITEM_COUNT) UseCase<Void, Observable<Integer>> delItemsCountUseCase,
            @Named(GET_BASKET) UseCase<Void, Observable<List<BasketItem>>> getBasketItemsUseCase,
            @Named(SET_VIEW_MODE) UseCase<Boolean, Void> setViewModeUseCase,
            @Named(DELETE_SELECTED_SHOWCASE_ITEMS) UseCase<Void, Completable> deleteSelectedShowcaseItemsUseCase
    ) {
        mAddItemUseCase = addItemUseCase;
        mDelModeUseCase = delModeUseCase;
        mToggleBasketCheck = toggleBasketCheck;
        mRemoveMarkedBasketItemsUseCase = removeMarkedBasketItemsUseCase;
        mResetItemsUseCase = resetItemsUseCase;
        mSelectCategoryUseCase = selectCategoryUseCase;
        mUpdateItemsUseCase = updateItemsUseCase;
        mGetBasketItemsUseCase = getBasketItemsUseCase;
        mSetViewModeUseCase = setViewModeUseCase;
        mDeleteSelectedShowcaseItemsUseCase = deleteSelectedShowcaseItemsUseCase;

        mDisposable.addAll(
                getViewModeUseCase.execute(null)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(isShowcaseMode -> {
                            viewModeData.setValue(isShowcaseMode);
                            showcaseModeState.setState(isShowcaseMode);
                            if (isShowcaseMode) {
                                removeByTapInBasketModeState.setState(false);
                                removeCountState.setState(0);
                                markCountState.setState(0);
                            }
                        }),

                getDelModeUseCase.execute(null)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(isDelMode -> deleteModeData.setValue(isDelMode)),

                delItemsCountUseCase.execute(null)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(delItemsCount -> deleteItemsCountData.setValue(delItemsCount))
        );

        mGuide = buildGuide(guidePrefs); // TODO: Replace with injection after the guide refactoring

        if (appPrefs.isHintsTurnedOn()) {
            mGuide.observe(this);
            mGuide.start();
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mDisposable.clear();
        mGuide.removeObserver(this);
    }

    /**
     * Show in the Showcase only items with specified tag
     *
     * @param tag item type or category
     */
    public void setFilter(String tag) {
        mSelectCategoryUseCase.execute(tag);
    }

    /**
     * Return default showcase items
     *
     * @param fullReset if true delete all user items
     */
    public void resetShowcase(boolean fullReset) {
        mDisposable.add(
                mResetItemsUseCase
                        .execute(fullReset)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> resetShowcaseEvent.setValue(true),
                                error -> resetShowcaseEvent.setValue(false)
                        )
        );
    }

    /**
     * Update displayed item names that were changed with localization (exclude user items)
     */
    public void updateLocaleNames() {
        mUpdateItemsUseCase.execute(null);
    }

    public LiveData<Boolean> showcaseModeState() {
        return viewModeData;
    }

    public boolean isShowcaseMode() {
        if (viewModeData.getValue() == null) return true;
        return viewModeData.getValue();
    }

    public void setViewMode(boolean isShowcaseMode) {
        mSetViewModeUseCase.execute(isShowcaseMode);
        mGuide.onUserEvent(GuideContract.GUIDE_CHANGE_MODE);
    }

    public List<? extends ItemModel> getBasketItems() {
        return mGetBasketItemsUseCase.execute(null).blockingFirst();
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
        GuideCase famHelpCase = new GuideCaseImpl(GuideContract.GUIDE_BASKET_MENU_HELP,
                prefs.getBoolean(GuideContract.GUIDE_BASKET_MENU_HELP, false));

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
                .addCase(famHelpCase)
                .require(new Requirement(markCountState) {
                    @Override
                    public boolean check() {
                        return markCountState.getState() > 1;
                    }
                })
                .build();
    }

    public void onEventHappened(String eventKey) {
        mGuide.onUserEvent(eventKey);
    }

    /**
     * Complete current guide case.
     */
    public void onHintClick() {
        mGuide.onUserEvent(mGuide.currentCase());
    }


    public LiveEvent<Boolean> getResetShowcaseEvent() {
        return resetShowcaseEvent;
    }

    public LiveEvent<Boolean> getRemoveCheckedBasketItemsEvent() {
        return deleteCheckedEvent;
    }

    public LiveData<Boolean> deleteModeData() {
        return deleteModeData;
    }

    public LiveData<Integer> deleteItemsCountData() {
        return deleteItemsCountData;
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
        ContextualGuide guide = (ContextualGuide) mGuide;
        if (basketSizeState.getState() > 0) {
            guide.completeCase(GuideContract.GUIDE_ADD_ITEM_BY_TAP);
        }
        if (markCountState.getState() > 0) {
            guide.completeCase(GuideContract.GUIDE_CHECK_ITEM);
        }
        if (delModeState.getState()) {
            guide.completeCase(GuideContract.GUIDE_DEL_MODE);
            guide.completeCase(GuideContract.GUIDE_DEL_SELECTED_ITEMS);
        }
    }

    @Override
    public void onGuideFinish() {
        mCurGuideCaseData.setValue(null);
        newItemAddedState.setState(false);
        removeByTapInBasketModeState.setState(false);
        markCountState.setState(0);
        removeCountState.setState(0);
    }

    @Override
    public void onGuideCaseStart(String caseKey) {
        mCurGuideCaseData.setValue(caseKey);

        if (TextUtils.isEmpty(caseKey)) return;
        Bundle bundle = new Bundle();
        bundle.putString("guidecase_name", caseKey);
        App.getAnalytics().logEvent(FirebaseAnalytics.Event.TUTORIAL_BEGIN, bundle);
    }

    @Override
    public void onGuideCaseComplete(String caseKey) {
        mCompletedGuideCase.setValue(caseKey);
    }


    /* Other methods */

    public void setOrientationState(boolean isLandscape) {
        landscapeState.setState(isLandscape);
    }

    public void onDeleteSelectedShowcaseItems() {
        Disposable d = mDeleteSelectedShowcaseItemsUseCase
                .execute(null)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::onCloseDelMode,
                        e -> Log.e(TAG, "Failed to delete showcase items", e)
                );
        mDisposable.add(d);
    }

    public void onCloseDelMode() {
        mGuide.onUserEvent(GuideContract.GUIDE_DEL_SELECTED_ITEMS);
        mDelModeUseCase.execute(false);
    }

    public void onCheckBasket() {
        mGuide.onUserEvent(GuideContract.GUIDE_BASKET_MENU_HELP);
        mToggleBasketCheck.execute(null);
    }

    public void onDelCheckedBasketItems() {
        mGuide.onUserEvent(GuideContract.GUIDE_BASKET_MENU_HELP);
        mDisposable.add(
                mRemoveMarkedBasketItemsUseCase
                        .execute(null)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> deleteCheckedEvent.setValue(true),
                                error -> deleteCheckedEvent.setValue(false)
                        )
        );
    }

    /**
     * Create a new item in the Showcase if it doesn't already exist,
     * then add it to the Basket
     */
    public void onSearch(String name) {
        mDisposable.add(
                mAddItemUseCase
                        .execute(name)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(resultCode -> {
                                    if (resultCode == AddItem.NEW_ITEM_ADDED) {
                                        newItemAddedState.setState(true);
                                    }
                                }
                        )
        );
    }

}
