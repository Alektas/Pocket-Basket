package alektas.pocketbasket.domain.usecases.basket;

import javax.inject.Inject;

import alektas.pocketbasket.domain.BasketRepository;
import alektas.pocketbasket.domain.usecases.UseCase;
import io.reactivex.Completable;

/**
 * Delete all marked items in the Basket.
 */
public class RemoveCheckedBasketItems implements UseCase<Void, Completable> {
    private BasketRepository mRepository;

    @Inject
    public RemoveCheckedBasketItems(BasketRepository repository) {
        mRepository = repository;
    }

    @Override
    public Completable execute(Void v) {
        return mRepository.removeCheckedBasketItems();
    }

}
