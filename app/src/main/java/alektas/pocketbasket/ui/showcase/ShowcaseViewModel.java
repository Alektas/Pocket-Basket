package alektas.pocketbasket.ui.showcase;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

import alektas.pocketbasket.App;
import alektas.pocketbasket.data.RepositoryImpl;
import alektas.pocketbasket.domain.Repository;
import alektas.pocketbasket.domain.entities.ShowcaseItemModel;
import alektas.pocketbasket.domain.usecases.DelModeUseCase;
import alektas.pocketbasket.domain.usecases.SelectShowcaseItem;
import alektas.pocketbasket.guide.GuideContract;
import alektas.pocketbasket.guide.domain.ContextualGuide;
import alektas.pocketbasket.guide.domain.Guide;
import alektas.pocketbasket.ui.ActivityViewModel;
import alektas.pocketbasket.ui.UiContract;

public class ShowcaseViewModel extends AndroidViewModel {
    private static final String TAG = "ShowcaseViewModel";
    private Repository mRepository;
    private Guide mGuide;
    private MutableLiveData<List<ShowcaseItemModel>> mShowcaseData = new MutableLiveData<>();
    private MutableLiveData<Boolean> delModeState = new MutableLiveData<>();
    private MutableLiveData<Boolean> showcaseModeData = new MutableLiveData<>();
    private boolean showcaseModeState = UiContract.IS_DEFAULT_MODE_SHOWCASE;

    public ShowcaseViewModel(@NonNull Application application) {
        super(application);
        mRepository = RepositoryImpl.getInstance(application);
        mRepository.getShowcaseData().observe(mShowcaseData::setValue);
        mRepository.showcaseModeData().observe(state -> {
            showcaseModeData.setValue(state);
            showcaseModeState = state;
        });
        mRepository.delModeData().observe((delMode) -> {
            delModeState.setValue(delMode);
            ActivityViewModel.delModeState.setState(delMode);
        });
        mGuide = ContextualGuide.getInstance();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mRepository.getShowcaseData().clearObservers();
        mRepository.showcaseModeData().clearObservers();
        mRepository.delModeData().clearObservers();
        mRepository = null;
    }

    public MutableLiveData<Boolean> getShowcaseModeData() {
        return showcaseModeData;
    }

    public LiveData<List<ShowcaseItemModel>> getShowcaseData() {
        return mShowcaseData;
    }


    /* On Click */

    public boolean onItemLongClick(ShowcaseItemModel item) {
        if (!isDelMode()) enableDelMode();
        mRepository.selectForDeleting(item);
        return true;
    }

    public void onItemClick(ShowcaseItemModel item) {
        if (isDelMode()) {
            mRepository.selectForDeleting(item);
            return;
        }
        new SelectShowcaseItem(mRepository).execute(item.getKey(), (isAdded) -> {
            if (isAdded) {
                mGuide.onUserEvent(GuideContract.GUIDE_ADD_ITEM_BY_TAP);

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, item.getKey());
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, item.getName());
                bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, item.getTagRes());
                App.getAnalytics().logEvent(FirebaseAnalytics.Event.ADD_TO_CART, bundle);
            } else if (!showcaseModeState) {
                ActivityViewModel.removeByTapInBasketModeState.setState(true);
                ActivityViewModel.removeCountState
                        .setState(ActivityViewModel.removeCountState.getState() + 1);
            }
        });
    }


    /* Handle delete mode */

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
    private void enableDelMode() {
        mGuide.onUserEvent(GuideContract.GUIDE_DEL_MODE);
        new DelModeUseCase(mRepository).execute(true, null);
    }

}
