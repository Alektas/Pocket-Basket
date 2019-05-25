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
import alektas.pocketbasket.domain.usecases.MarkBasketItem;
import alektas.pocketbasket.domain.usecases.MoveBasketItem;
import alektas.pocketbasket.domain.usecases.RemoveItemFromBasket;
import alektas.pocketbasket.domain.usecases.UseCase;
import alektas.pocketbasket.guide.Guide;
import alektas.pocketbasket.guide.GuideContract;

public class BasketViewModel extends AndroidViewModel {
    private Repository mRepository;
    private Guide mGuide;
    private MutableLiveData<List<BasketItemModel>> mBasketData = new MutableLiveData<>();

    public BasketViewModel(@NonNull Application application) {
        super(application);
        mRepository = RepositoryImpl.getInstance(application);
        mRepository.getBasketData().observe(mBasketData::setValue);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mRepository.getBasketData().clearObservers();
        mRepository = null;
        mGuide = null;
    }

    public void setGuide(Guide guide) {
        mGuide = guide;
    }

    public Guide getGuide() {
        return mGuide;
    }

    public LiveData<List<BasketItemModel>> getBasketData() {
        return mBasketData;
    }

    /**
     * Caused by dragging items in the Basket.
     * Save items' positions in the Basket like a positions in the list.
     * @param names names of all basket items
     */
    public void updatePositions(List<String> names) {
        UseCase<List<String>, Void> useCase = new MoveBasketItem(mRepository);
        useCase.execute(names, null);
        mGuide.onCaseHappened(GuideContract.GUIDE_MOVE_ITEM);
    }

    /**
     * Check or uncheck item in the Basket
     */
    public void markItem(String name) {
        UseCase<String, Void> useCase = new MarkBasketItem(mRepository);
        useCase.execute(name, null);
        mGuide.onCaseHappened(GuideContract.GUIDE_CHECK_ITEM);
    }

    public void removeFromBasket(String name) {
        new RemoveItemFromBasket(mRepository).execute(name, null);
        mGuide.onCaseHappened(GuideContract.GUIDE_REMOVE_ITEM);
    }

}
