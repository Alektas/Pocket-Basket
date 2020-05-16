package alektas.pocketbasket.di.basket;

import java.util.List;

import javax.inject.Named;

import alektas.pocketbasket.data.db.entities.BasketItem;
import alektas.pocketbasket.domain.BasketRepository;
import alektas.pocketbasket.domain.ShowcaseRepository;
import alektas.pocketbasket.domain.entities.ItemModel;
import alektas.pocketbasket.domain.usecases.UseCase;
import alektas.pocketbasket.domain.usecases.basket.ChangeBasketPositions;
import alektas.pocketbasket.domain.usecases.basket.GetBasket;
import alektas.pocketbasket.domain.usecases.basket.MoveBasketItemToTop;
import alektas.pocketbasket.domain.usecases.basket.RemoveBasketItem;
import alektas.pocketbasket.domain.usecases.basket.RemoveCheckedBasketItems;
import alektas.pocketbasket.domain.usecases.basket.ToggleBasketCheck;
import alektas.pocketbasket.domain.usecases.basket.ToggleBasketItemCheck;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import io.reactivex.Completable;
import io.reactivex.Observable;

@Module
public abstract class BasketUseCasesModule {
    public static final String REMOVE_BASKET_ITEM_BY_KEY = "REMOVE_BASKET_ITEM_BY_KEY";
    public static final String CHANGE_BASKET_POSITIONS = "CHANGE_BASKET_POSITIONS";
    public static final String TOGGLE_BASKET_CHECK = "TOGGLE_BASKET_CHECK";
    public static final String TOGGLE_BASKET_ITEM_CHECK = "TOGGLE_BASKET_ITEM_CHECK";
    public static final String MOVE_BASKET_ITEM_TO_TOP = "MOVE_BASKET_ITEM_TO_TOP";
    public static final String REMOVE_CHECKED_BASKET_ITEMS = "REMOVE_CHECKED_BASKET_ITEMS";
    public static final String GET_BASKET = "GET_BASKET";

    @Provides
    @Named(REMOVE_BASKET_ITEM_BY_KEY)
    static UseCase<String, Completable> removeFromBasketByKey(
            BasketRepository basketRepository,
            ShowcaseRepository showcaseRepository
    ) {
        return new RemoveBasketItem(basketRepository, showcaseRepository, false);
    }

    @Binds
    @Named(CHANGE_BASKET_POSITIONS)
    abstract UseCase<List<ItemModel>, Void> changeBasketPositions(ChangeBasketPositions useCase);

    @Binds
    @Named(TOGGLE_BASKET_CHECK)
    abstract UseCase<Void, Void> toggleBasketCheck(ToggleBasketCheck useCase);

    @Binds
    @Named(TOGGLE_BASKET_ITEM_CHECK)
    abstract UseCase<String, Void> toggleBasketItemCheck(ToggleBasketItemCheck useCase);

    @Binds
    @Named(MOVE_BASKET_ITEM_TO_TOP)
    abstract UseCase<String, Void> moveItemToTop(MoveBasketItemToTop useCase);

    @Binds
    @Named(REMOVE_CHECKED_BASKET_ITEMS)
    abstract UseCase<Void, Completable> removeMarkedBasketItems(RemoveCheckedBasketItems useCase);

    @Binds
    @Named(GET_BASKET)
    abstract UseCase<Void, Observable<List<BasketItem>>> getBasket(GetBasket useCase);

}
