package alektas.pocketbasket.domain.usecases;

import javax.inject.Inject;

import alektas.pocketbasket.domain.Repository;
import io.reactivex.Completable;

public class CleanBasket implements UseCase<Void, Completable> {
    private Repository mRepository;

    @Inject
    public CleanBasket(Repository repository) {
        mRepository = repository;
    }

    @Override
    public Completable execute(Void v) {
        return mRepository.cleanBasket();
    }
}
