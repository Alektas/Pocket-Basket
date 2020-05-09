package alektas.pocketbasket.domain.usecases.basket;

import java.util.List;

import javax.inject.Inject;

import alektas.pocketbasket.data.db.entities.BasketItem;
import alektas.pocketbasket.domain.BasketRepository;
import alektas.pocketbasket.domain.usecases.UseCase;
import io.reactivex.Observable;

public class GetBasket implements UseCase<Void, Observable<List<BasketItem>>> {
    private BasketRepository mRepository;

    @Inject
    public GetBasket(BasketRepository repository) {
        mRepository = repository;
    }

    @Override
    public Observable<List<BasketItem>> execute(Void request) {
        return mRepository.getBasketData();
    }

}
