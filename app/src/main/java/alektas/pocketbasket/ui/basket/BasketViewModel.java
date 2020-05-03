package alektas.pocketbasket.ui.basket;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import alektas.pocketbasket.data.db.entities.BasketItem;
import alektas.pocketbasket.domain.Repository;
import alektas.pocketbasket.domain.entities.ItemModel;
import alektas.pocketbasket.domain.usecases.UseCase;
import alektas.pocketbasket.guide.GuideContract;
import alektas.pocketbasket.guide.domain.Guide;
import alektas.pocketbasket.ui.ActivityViewModel;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

import static alektas.pocketbasket.di.UseCasesModule.CHANGE_ITEM_POSITIONS_USE_CASE;
import static alektas.pocketbasket.di.UseCasesModule.MARK_BASKET_ITEM_USE_CASE;
import static alektas.pocketbasket.di.UseCasesModule.MOVE_ITEM_TO_TOP_USE_CASE;
import static alektas.pocketbasket.di.UseCasesModule.REMOVE_BY_KEY_USE_CASE;

public class BasketViewModel extends ViewModel {
    private Repository mRepository;
    private Guide mGuide;
    private UseCase<List<ItemModel>, Void> mChangePositionsUseCase;
    private UseCase<String, Void> mMarkBasketItemUseCase;
    private UseCase<String, Completable> mRemoveBasketItemUseCase;
    private UseCase<String, Void> mMoveItemToTopUseCase;
    private CompositeDisposable mDisposable = new CompositeDisposable();
    private MutableLiveData<List<BasketItem>> mBasketData = new MutableLiveData<>();

    @Inject
    public BasketViewModel(
            Repository repository,
            Guide guide,
            @Named(CHANGE_ITEM_POSITIONS_USE_CASE) UseCase<List<ItemModel>, Void> changePositionsUseCase,
            @Named(MARK_BASKET_ITEM_USE_CASE) UseCase<String, Void> markBasketItemUseCase,
            @Named(REMOVE_BY_KEY_USE_CASE) UseCase<String, Completable> removeBasketItemUseCase,
            @Named(MOVE_ITEM_TO_TOP_USE_CASE) UseCase<String, Void> moveItemToTopUseCase
    ) {
        mChangePositionsUseCase = changePositionsUseCase;
        mMarkBasketItemUseCase = markBasketItemUseCase;
        mRemoveBasketItemUseCase = removeBasketItemUseCase;
        mMoveItemToTopUseCase = moveItemToTopUseCase;

        mRepository = repository;
        mDisposable.add(
                mRepository.getBasketData()
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
