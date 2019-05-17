package alektas.pocketbasket.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import alektas.pocketbasket.data.RepositoryImpl;
import alektas.pocketbasket.db.entities.Item;
import alektas.pocketbasket.domain.Repository;
import alektas.pocketbasket.domain.entities.ItemModel;
import alektas.pocketbasket.domain.usecases.SelectShowcaseItem;
import alektas.pocketbasket.guide.Guide;
import alektas.pocketbasket.guide.GuideContract;

public class ShowcaseViewModel extends AndroidViewModel {
    private Repository mRepository;
    private Guide mGuide;
    /**
     * Items selected by the user for removal from the Showcase
     */
    private List<Item> mDelItems;
    private MutableLiveData<List<? extends ItemModel>> mShowcaseData = new MutableLiveData<>();
    private MutableLiveData<Integer> selectedItemPosition = new MutableLiveData<>();
    private MutableLiveData<Boolean> delModeState = new MutableLiveData<>();
    private MutableLiveData<Boolean> showcaseModeState = new MutableLiveData<>();

    public ShowcaseViewModel(@NonNull Application application) {
        super(application);
        mRepository = RepositoryImpl.getInstance(application);
        mRepository.getShowcaseData().observe(mShowcaseData::setValue);
        mRepository.showcaseModeState().observe(showcaseModeState::setValue);
        mRepository.delModeState().observe(delModeState::setValue);
        mDelItems = new ArrayList<>();
    }

    public void setGuide(Guide guide) {
        mGuide = guide;
    }

    public Guide getGuide() {
        return mGuide;
    }

    public LiveData<List<? extends ItemModel>> getShowcaseData() {
        return mShowcaseData;
    }

    public LiveData<Boolean> showcaseModeState() {
        return showcaseModeState;
    }

    public boolean isItemInBasket(String name) {
        return mRepository.isItemInBasket(name);
    }


    /* On Click */

    public boolean onItemLongClick(Item item, RecyclerView.ViewHolder holder) {
        if (!isDelMode() && isDelModeAllowed()) {
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
            new SelectShowcaseItem(mRepository).execute(item.getName(), null);
        }
    }


    /* Handle delete mode */

    /**
     * Turn off the Delete Mode in which user can delete items from the Showcase
     * with deleting selected items.
     */
    public void deleteSelectedItems() {
        mRepository.deleteItems(mDelItems);
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

    // Used in the showcase data binding
    public List<Item> getDelItems() {
        return mDelItems;
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
        if (!mGuide.isGuideStarted()) return true;
        return GuideContract.GUIDE_DEL_MODE.equals(mGuide.currentCaseKey())
                || GuideContract.GUIDE_DEL_ITEMS.equals(mGuide.currentCaseKey());
    }
}
