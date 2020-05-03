package alektas.pocketbasket.ui.showcase;

import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import alektas.pocketbasket.App;
import alektas.pocketbasket.data.db.entities.ShowcaseItem;
import alektas.pocketbasket.domain.Repository;
import alektas.pocketbasket.domain.usecases.SelectShowcaseItem;
import alektas.pocketbasket.domain.usecases.UseCase;
import alektas.pocketbasket.guide.GuideContract;
import alektas.pocketbasket.guide.domain.Guide;
import alektas.pocketbasket.ui.ActivityViewModel;
import alektas.pocketbasket.ui.UiContract;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

import static alektas.pocketbasket.di.UseCasesModule.DEL_MODE_USE_CASE;
import static alektas.pocketbasket.di.UseCasesModule.SELECT_SHOWCASE_ITEM_USE_CASE;

public class ShowcaseViewModel extends ViewModel {
    private static final String TAG = "ShowcaseViewModel";
    private Repository mRepository;
    private Guide mGuide;
    private UseCase<String, Single<Integer>> mSelectItemUseCase;
    private UseCase<Boolean, Void> mDelModeUseCase;
    private CompositeDisposable mDisposable = new CompositeDisposable();
    private MutableLiveData<List<ShowcaseItem>> mShowcaseData = new MutableLiveData<>();
    private MutableLiveData<Boolean> delModeState = new MutableLiveData<>();
    private MutableLiveData<Boolean> showcaseModeData = new MutableLiveData<>();
    private boolean isShowcaseMode = UiContract.IS_DEFAULT_MODE_SHOWCASE;

    @Inject
    public ShowcaseViewModel(
            Repository repository,
            Guide guide,
            @Named(SELECT_SHOWCASE_ITEM_USE_CASE) UseCase<String, Single<Integer>> selectItemUseCase,
            @Named(DEL_MODE_USE_CASE) UseCase<Boolean, Void> delModeUseCase
    ) {
        mSelectItemUseCase = selectItemUseCase;
        mDelModeUseCase = delModeUseCase;

        mRepository = repository;
        mGuide = guide;
        mDisposable.addAll(
                mRepository.getShowcaseData()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(items -> mShowcaseData.setValue(items)),

                mRepository.observeViewMode()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(isShowcaseMode -> {
                            showcaseModeData.setValue(isShowcaseMode);
                            this.isShowcaseMode = isShowcaseMode;
                        }),

                mRepository.observeDelMode()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(isDelMode -> {
                            delModeState.setValue(isDelMode);
                            ActivityViewModel.delModeState.setState(isDelMode);
                        })
        );

    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mDisposable.clear();
        mRepository = null;
    }

    public LiveData<List<ShowcaseItem>> getShowcaseData() {
        return mShowcaseData;
    }


    /* On Click */

    public boolean onItemLongClick(ShowcaseItem item) {
        if (!isDelMode()) enableDelMode();
        mRepository.toggleDeletingSelection(item);
        return true;
    }

    public void onItemClick(ShowcaseItem item) {
        if (isDelMode()) {
            mRepository.toggleDeletingSelection(item);
            return;
        }
        mDisposable.add(mSelectItemUseCase
                .execute(item.getKey())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resultCode -> {
                    if (resultCode == SelectShowcaseItem.ITEM_ADDED_TO_BASKET) {
                        mGuide.onUserEvent(GuideContract.GUIDE_ADD_ITEM_BY_TAP);
                        sendAnalytics(item);
                        return;
                    }
                    if (resultCode == SelectShowcaseItem.ITEM_REMOVED_FROM_BASKET && !isShowcaseMode) {
                        ActivityViewModel.removeByTapInBasketModeState.setState(true);
                        ActivityViewModel.removeCountState
                                .setState(ActivityViewModel.removeCountState.getState() + 1);
                    }
                }));
    }

    private void sendAnalytics(ShowcaseItem item) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, item.getKey());
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, item.getName());
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, item.getTagRes());
        App.getAnalytics().logEvent(FirebaseAnalytics.Event.ADD_TO_CART, bundle);
    }


    /* Handle delete mode */

    public boolean isDelMode() {
        if (delModeState.getValue() == null) return false;
        return delModeState.getValue();
    }

    /**
     * Turn on/off the Delete Mode in which user can delete items from the Showcase
     */
    private void enableDelMode() {
        mGuide.onUserEvent(GuideContract.GUIDE_DEL_MODE);
        mDelModeUseCase.execute(true);
    }

}
