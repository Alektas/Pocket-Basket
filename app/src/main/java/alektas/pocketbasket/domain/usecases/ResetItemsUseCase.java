package alektas.pocketbasket.domain.usecases;

import alektas.pocketbasket.domain.Repository;
import io.reactivex.Completable;

public class ResetItemsUseCase implements UseCase<Boolean, Completable> {
    private Repository mRepository;

    public ResetItemsUseCase(Repository repository) {
        mRepository = repository;
    }

    @Override
    public Completable execute(Boolean fullReset) {
        if (fullReset) {
            return mRepository.resetShowcase();
        } else {
            return mRepository.restoreShowcase();
        }
    }
}
