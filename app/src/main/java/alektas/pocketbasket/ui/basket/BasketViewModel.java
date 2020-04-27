package alektas.pocketbasket.ui.basket;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import alektas.pocketbasket.data.RepositoryImpl;
import alektas.pocketbasket.data.db.entities.BasketItem;
import alektas.pocketbasket.domain.Repository;
import alektas.pocketbasket.domain.entities.ItemModel;
import alektas.pocketbasket.domain.usecases.ChangeItemsPositions;
import alektas.pocketbasket.domain.usecases.MarkBasketItem;
import alektas.pocketbasket.domain.usecases.MoveItemToTopUseCase;
import alektas.pocketbasket.domain.usecases.RemoveItemFromBasket;
import alektas.pocketbasket.guide.GuideContract;
import alektas.pocketbasket.guide.domain.ContextualGuide;
import alektas.pocketbasket.guide.domain.Guide;
import alektas.pocketbasket.ui.ActivityViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class BasketViewModel extends AndroidViewModel {
    private Repository mRepository;
    private Guide mGuide;
    private CompositeDisposable mDisposable = new CompositeDisposable();
    private MutableLiveData<List<BasketItem>> mBasketData = new MutableLiveData<>();

    public BasketViewModel(@NonNull Application application) {
        super(application);
        mRepository = RepositoryImpl.getInstance(application);
        mDisposable.add(
                mRepository.getBasketData()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(basketItems -> {
                            mBasketData.setValue(basketItems);
                            ActivityViewModel.basketSizeState.setState(basketItems.size());
                        })
        );
        mGuide = ContextualGuide.getInstance();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mDisposable.clear();
        mRepository = null;
    }

    public LiveData<List<BasketItem>> getBasketData() {
        return mBasketData;
    }

    /**
     * Caused by dragging items in the Basket.
     * Save items' positions in the Basket like a positions in the list.
     *
     * @param names names of all basket items
     */
    public void updatePositions(List<ItemModel> names) {
        new ChangeItemsPositions(mRepository).execute(names);
        mGuide.onUserEvent(GuideContract.GUIDE_MOVE_ITEM);
    }

    /**
     * Check or uncheck item in the Basket
     */
    public void markItem(String name) {
        new MarkBasketItem(mRepository).execute(name);
        ActivityViewModel.markCountState.setState(ActivityViewModel.markCountState.getState() + 1);
        mGuide.onUserEvent(GuideContract.GUIDE_CHECK_ITEM);
    }

    public void removeFromBasket(String key) {
        new RemoveItemFromBasket(mRepository).execute(key);
        ActivityViewModel.removeCountState.setState(ActivityViewModel.removeCountState.getState() + 1);
        mGuide.onUserEvent(GuideContract.GUIDE_SWIPE_REMOVE_ITEM);
    }

    public void onItemDoubleClick(String key) {
        new MoveItemToTopUseCase(mRepository).execute(key);
    }

}
