package alektas.pocketbasket.ui.basket;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import alektas.pocketbasket.domain.entities.BasketItem;
import alektas.pocketbasket.domain.usecases.UseCase;
import alektas.pocketbasket.guide.GuideContract;
import alektas.pocketbasket.guide.domain.Guide;
import alektas.pocketbasket.ui.ActivityViewModel;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

import static alektas.pocketbasket.di.basket.BasketUseCasesModule.CHANGE_BASKET_POSITIONS;
import static alektas.pocketbasket.di.basket.BasketUseCasesModule.GET_BASKET;
import static alektas.pocketbasket.di.basket.BasketUseCasesModule.MOVE_BASKET_ITEM_TO_TOP;
import static alektas.pocketbasket.di.basket.BasketUseCasesModule.REMOVE_BASKET_ITEM_BY_KEY;
import static alektas.pocketbasket.di.basket.BasketUseCasesModule.TOGGLE_BASKET_ITEM_CHECK;

public class BasketViewModel extends ViewModel {
    private Guide mGuide;
    private UseCase<List<BasketItem>, Void> mChangePositionsUseCase;
    private UseCase<String, Void> mMarkBasketItemUseCase;
    private UseCase<String, Completable> mRemoveBasketItemUseCase;
    private UseCase<String, Void> mMoveItemToTopUseCase;
    private CompositeDisposable mDisposable = new CompositeDisposable();
    private MutableLiveData<List<BasketItem>> mBasketData = new MutableLiveData<>();

    @Inject
    public BasketViewModel(
            Guide guide,
            @Named(CHANGE_BASKET_POSITIONS) UseCase<List<BasketItem>, Void> changePositionsUseCase,
            @Named(TOGGLE_BASKET_ITEM_CHECK) UseCase<String, Void> markBasketItemUseCase,
            @Named(REMOVE_BASKET_ITEM_BY_KEY) UseCase<String, Completable> removeBasketItemUseCase,
            @Named(MOVE_BASKET_ITEM_TO_TOP) UseCase<String, Void> moveItemToTopUseCase,
            @Named(GET_BASKET) UseCase<Void, Observable<List<BasketItem>>> getBasketUseCase
    ) {
        mChangePositionsUseCase = changePositionsUseCase;
        mMarkBasketItemUseCase = markBasketItemUseCase;
        mRemoveBasketItemUseCase = removeBasketItemUseCase;
        mMoveItemToTopUseCase = moveItemToTopUseCase;

        mDisposable.add(
                getBasketUseCase.execute(null)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(basketItems -> {
                            mBasketData.setValue(basketItems);
                            ActivityViewModel.basketSizeState.setState(basketItems.size());
                        })
        );
        mGuide = guide;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mDisposable.clear();
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
    public void updatePositions(List<BasketItem> names) {
        mChangePositionsUseCase.execute(names);
        mGuide.onUserEvent(GuideContract.GUIDE_MOVE_ITEM);
    }

    /**
     * Check or uncheck item in the Basket
     */
    public void markItem(String name) {
        mMarkBasketItemUseCase.execute(name);
        ActivityViewModel.markCountState.setState(ActivityViewModel.markCountState.getState() + 1);
        mGuide.onUserEvent(GuideContract.GUIDE_CHECK_ITEM);
    }

    public void removeFromBasket(String key) {
        mRemoveBasketItemUseCase.execute(key);
        ActivityViewModel.removeCountState.setState(ActivityViewModel.removeCountState.getState() + 1);
        mGuide.onUserEvent(GuideContract.GUIDE_SWIPE_REMOVE_ITEM);
    }

    public void onItemDoubleClick(String key) {
        mMoveItemToTopUseCase.execute(key);
    }

}
