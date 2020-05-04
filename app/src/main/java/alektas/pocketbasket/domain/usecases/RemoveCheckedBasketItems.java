package alektas.pocketbasket.domain.usecases;

import javax.inject.Inject;

import alektas.pocketbasket.domain.Repository;
import io.reactivex.Completable;

/**
 * Delete all marked items in the Basket.
 */
public class RemoveCheckedBasketItems implements UseCase<Void, Completable> {
    private Repository mRepository;

    @Inject
    public RemoveCheckedBasketItems(Repository repository) {
        mRepository = repository;
    }

    @Override
    public Completable execute(Void v) {
        return mRepository.removeCheckedBasketItems();
    }

}
