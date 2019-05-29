package alektas.pocketbasket.ui;

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
import alektas.pocketbasket.data.RepositoryImpl;
import alektas.pocketbasket.domain.Repository;
import alektas.pocketbasket.domain.entities.ItemModel;
import alektas.pocketbasket.domain.usecases.AddItemUseCase;
import alektas.pocketbasket.domain.usecases.MarkAllBasketItems;
import alektas.pocketbasket.domain.usecases.PutItemToBasket;
import alektas.pocketbasket.domain.usecases.RemoveMarkedItems;
import alektas.pocketbasket.domain.usecases.ResetItemsUseCase;
import alektas.pocketbasket.domain.usecases.SelectCategoryUseCase;
import alektas.pocketbasket.domain.usecases.UpdateItemsUseCase;
import alektas.pocketbasket.domain.usecases.UseCase;
import alektas.pocketbasket.guide.GuideContract;
import alektas.pocketbasket.guide.GuideObserver;
import alektas.pocketbasket.guide.domain.Guide;
import alektas.pocketbasket.guide.domain.SequentialGuide;

public class ActivityViewModel extends AndroidViewModel implements GuideObserver {
    private static final String TAG = "ActivityViewModel";
    private Guide mGuide;
    private Repository mRepository;

    private MutableLiveData<Boolean> showcaseModeState = new MutableLiveData<>();
    private MutableLiveData<Boolean> guideModeState = new MutableLiveData<>();
    private MutableLiveData<String> mCurGuideCase = new MutableLiveData<>();

    public ActivityViewModel(@NonNull Application application) {
        super(application);
        mRepository = RepositoryImpl.getInstance(application);
        mRepository.showcaseModeState().observe(showcaseModeState::setValue);
        mGuide = SequentialGuide.getInstance();
        mGuide.observe(this);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mRepository.showcaseModeState().clearObservers();
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

    public void onStartGuideSelected() {
        if (mGuide == null) {
            Log.e(TAG, "start: to start " +
                            "you need to set the Guide by #setGuide method",
                    new NullPointerException("SequentialGuide is null"));
        }
        mGuide.start();
    }

    public void onEventHappened(String eventKey) {
        mGuide.onCaseHappened(eventKey);
    }

    /**
     * Finish guide during any case.
     * Warning! Do not invoke it in the {@link GuideObserver#onGuideFinish()}
     * to avoid the infinity loop.
     */
    public void onSkipGuideBtnClick() {
        mGuide.finish();
    }

    public boolean isGuideMode() {
        if (guideModeState.getValue() == null) return false;
        return guideModeState.getValue();
    }

    public LiveData<Boolean> guideModeState() {
        return guideModeState;
    }

    public LiveData<String> curGuideCaseData() {
        return mCurGuideCase;
    }


    /* Guide listener interface methods */

    @Override
    public void onGuideStart() {
        guideModeState.setValue(true);

        Bundle startGuide = new Bundle();
        App.getAnalytics().logEvent(FirebaseAnalytics.Event.TUTORIAL_BEGIN, startGuide);
    }

    @Override
    public void onGuideFinish() {
        mCurGuideCase.setValue(null);
        guideModeState.setValue(false);

        Bundle finGuide = new Bundle();
        App.getAnalytics().logEvent(FirebaseAnalytics.Event.TUTORIAL_COMPLETE, finGuide);
    }

    @Override
    public void onGuideCaseStart(String caseKey) {
        mCurGuideCase.setValue(caseKey);
    }

    @Override
    public void onGuideCaseFinish(String caseKey) {

    }


    /* Other methods */

    public void cancelDelMode() {
        mRepository.setDelMode(false);
    }

    public boolean isDelMode() {
        return mRepository.delModeState().getValue();
    }

    public void onFloatingMenuCalled() {
        mGuide.onCaseHappened(GuideContract.GUIDE_FLOATING_MENU);
    }

    public void onFabClick() {
        mGuide.onCaseHappened(GuideContract.GUIDE_FLOATING_MENU_HELP);
    }

}
