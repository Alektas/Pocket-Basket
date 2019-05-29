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
import alektas.pocketbasket.domain.usecases.SelectShowcaseItem;
import alektas.pocketbasket.guide.GuideContract;
import alektas.pocketbasket.guide.domain.Guide;
import alektas.pocketbasket.guide.domain.SequentialGuide;

public class ShowcaseViewModel extends AndroidViewModel {
    private Repository mRepository;
    private Guide mGuide;
    private MutableLiveData<List<ShowcaseItemModel>> mShowcaseData = new MutableLiveData<>();
    private MutableLiveData<Integer> selectedItemPosition = new MutableLiveData<>();
    private MutableLiveData<Boolean> delModeState = new MutableLiveData<>();
    private MutableLiveData<Boolean> showcaseModeState = new MutableLiveData<>();

    public ShowcaseViewModel(@NonNull Application application) {
        super(application);
        mRepository = RepositoryImpl.getInstance(application);
        mRepository.getShowcaseData().observe(mShowcaseData::setValue);
        mRepository.showcaseModeState().observe(showcaseModeState::setValue);
        mRepository.delModeState().observe(delModeState::setValue);
        mGuide = SequentialGuide.getInstance();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mRepository.getShowcaseData().clearObservers();
        mRepository.showcaseModeState().clearObservers();
        mRepository.delModeState().clearObservers();
        mRepository = null;
    }

    public LiveData<List<ShowcaseItemModel>> getShowcaseData() {
        return mShowcaseData;
    }

    public LiveData<Boolean> showcaseModeState() {
        return showcaseModeState;
    }


    /* On Click */

    public boolean onItemLongClick(ShowcaseItemModel item) {
        if (!isDelMode() && isDelModeAllowed()) {
            setDelMode(true);
        }
        mRepository.selectForDeleting(item);
        return true;
    }

    public void onItemClick(ShowcaseItemModel item) {
        if (isDelMode()) {
            mRepository.selectForDeleting(item);
            return;
        }
        new SelectShowcaseItem(mRepository).execute(item.getName(), (isAdded) -> {
            if (isAdded) {
                mGuide.onCaseHappened(GuideContract.GUIDE_ADD_ITEM);

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, item.getName());
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, item.getName());
                bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, item.getTagRes());
                App.getAnalytics().logEvent(FirebaseAnalytics.Event.ADD_TO_CART, bundle);
            } else {
                mGuide.onCaseHappened(GuideContract.GUIDE_REMOVE_ITEM);
            }
        });
    }


    /* Handle delete mode */

    /**
     * Turn off the Delete Mode in which user can delete items from the Showcase
     * with deleting selected items.
     */
    public void deleteSelectedItems() {
        mRepository.deleteSelectedItems();
        cancelDel();
    }

    /**
     * Turn off the Delete Mode in which user can delete items from the Showcase
     * without deleting selected items.
     */
    public void cancelDel() {
        setDelMode(false);
    }

    public LiveData<Integer> getSelectedItemPosition() {
        return selectedItemPosition;
    }

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
        if (delMode) {
            mGuide.onCaseHappened(GuideContract.GUIDE_DEL_MODE);
        } else {
            mGuide.onCaseHappened(GuideContract.GUIDE_DEL_ITEMS);
        }

        mRepository.setDelMode(delMode);
    }

    /**
     * When the Guide is started the Delete Mode allowed only in several cases
     */
    private boolean isDelModeAllowed() {
        if (!mGuide.isStarted()) return true;
        return GuideContract.GUIDE_DEL_MODE.equals(mGuide.currentCase())
                || GuideContract.GUIDE_DEL_ITEMS.equals(mGuide.currentCase());
    }
}
