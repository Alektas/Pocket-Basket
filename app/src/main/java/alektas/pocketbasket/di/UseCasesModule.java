package alektas.pocketbasket.di;

import javax.inject.Named;

import alektas.pocketbasket.domain.BasketRepository;
import alektas.pocketbasket.domain.ShowcaseRepository;
import alektas.pocketbasket.domain.usecases.GetViewMode;
import alektas.pocketbasket.domain.usecases.SetViewMode;
import alektas.pocketbasket.domain.usecases.UseCase;
import alektas.pocketbasket.domain.usecases.basket.CleanBasket;
import alektas.pocketbasket.domain.usecases.basket.RemoveBasketItem;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import io.reactivex.Completable;
import io.reactivex.Observable;

@Module
public interface UseCasesModule {
    String REMOVE_BASKET_ITEM_BY_NAME = "REMOVE_BASKET_ITEM_BY_NAME";
    String CLEAN_BASKET = "CLEAN_BASKET";
    String SET_VIEW_MODE = "SET_VIEW_MODE";
    String GET_VIEW_MODE = "GET_VIEW_MODE";

    @Provides
    @Named(REMOVE_BASKET_ITEM_BY_NAME)
    static UseCase<String, Completable> removeFromBasketByName(
            BasketRepository basketRepository,
            ShowcaseRepository showcaseRepository
    ) {
        return new RemoveBasketItem(basketRepository, showcaseRepository, true);
    }

    @Binds
    @Named(CLEAN_BASKET)
    UseCase<Void, Completable> cleanBasket(CleanBasket useCase);

    @Binds
    @Named(GET_VIEW_MODE)
    UseCase<Void, Observable<Boolean>> getViewMode(GetViewMode useCase);

    @Binds
    @Named(SET_VIEW_MODE)
    UseCase<Boolean, Void> setViewMode(SetViewMode useCase);

}
