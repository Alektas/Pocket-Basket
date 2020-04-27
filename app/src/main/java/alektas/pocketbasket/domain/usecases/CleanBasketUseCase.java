package alektas.pocketbasket.domain.usecases;

import alektas.pocketbasket.domain.Repository;
import io.reactivex.Completable;

public class CleanBasketUseCase implements UseCase<Void, Completable> {
    private Repository mRepository;

    public CleanBasketUseCase(Repository repository) {
        mRepository = repository;
    }

    @Override
    public Completable execute(Void v) {
        return mRepository.cleanBasket();
    }
}
