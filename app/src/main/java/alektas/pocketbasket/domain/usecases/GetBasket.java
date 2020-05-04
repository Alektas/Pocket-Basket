package alektas.pocketbasket.domain.usecases;

import java.util.List;

import javax.inject.Inject;

import alektas.pocketbasket.data.db.entities.BasketItem;
import alektas.pocketbasket.domain.Repository;
import io.reactivex.Observable;

public class GetBasket implements UseCase<Void, Observable<List<BasketItem>>> {
    private Repository mRepository;

    @Inject
    public GetBasket(Repository repository) {
        mRepository = repository;
    }

    @Override
    public Observable<List<BasketItem>> execute(Void request) {
        return mRepository.getBasketData();
    }

}
