package alektas.pocketbasket.ui.basket;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import alektas.pocketbasket.data.RepositoryImpl;
import alektas.pocketbasket.domain.Repository;
import alektas.pocketbasket.domain.entities.BasketItemModel;
import alektas.pocketbasket.domain.entities.ItemModel;
import alektas.pocketbasket.domain.usecases.ChangeItemsPositions;
import alektas.pocketbasket.domain.usecases.MarkBasketItem;
import alektas.pocketbasket.domain.usecases.RemoveItemFromBasket;
import alektas.pocketbasket.domain.usecases.UseCase;
import alektas.pocketbasket.domain.utils.Event;
import alektas.pocketbasket.guide.GuideContract;
import alektas.pocketbasket.guide.domain.ContextualGuide;
import alektas.pocketbasket.guide.domain.Guide;
import alektas.pocketbasket.ui.ActivityViewModel;

public class BasketViewModel extends AndroidViewModel {
    private Repository mRepository;
    private Guide mGuide;
    private MutableLiveData<List<BasketItemModel>> mBasketData = new MutableLiveData<>();
    private MutableLiveData<Event<Boolean>> scrollToTopEvent = new MutableLiveData<>();

    public BasketViewModel(@NonNull Application application) {
        super(application);
        mRepository = RepositoryImpl.getInstance(application);
        mRepository.getBasketData().observe(basketItems -> {
            mBasketData.setValue(basketItems);
            ActivityViewModel.basketSizeState.setState(basketItems.size());
        });
        mGuide = ContextualGuide.getInstance();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mRepository.getBasketData().clearObservers();
        mRepository = null;
    }

    public LiveData<List<BasketItemModel>> getBasketData() {
        return mBasketData;
    }

    public LiveData<Event<Boolean>> getScrollToTopEvent() {
        return scrollToTopEvent;
    }

    /**
     * Caused by dragging items in the Basket.
     * Save items' positions in the Basket like a positions in the list.
     * @param names names of all basket items
     */
    public void updatePositions(List<ItemModel> names) {
        new ChangeItemsPositions(mRepository).execute(names, null);
        mGuide.onUserEvent(GuideContract.GUIDE_MOVE_ITEM);
    }

    /**
     * Check or uncheck item in the Basket
     */
    public void markItem(String name) {
        UseCase<String, Void> useCase = new MarkBasketItem(mRepository);
        useCase.execute(name, null);
        ActivityViewModel.markCountState.setState(ActivityViewModel.markCountState.getState() + 1);
        mGuide.onUserEvent(GuideContract.GUIDE_CHECK_ITEM);
    }

    public void removeFromBasket(String key) {
        new RemoveItemFromBasket(mRepository).execute(key, null);
        ActivityViewModel.removeCountState.setState(ActivityViewModel.removeCountState.getState() + 1);
        mGuide.onUserEvent(GuideContract.GUIDE_SWIPE_REMOVE_ITEM);
    }

}
