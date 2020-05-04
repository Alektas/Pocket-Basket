package alektas.pocketbasket.domain.usecases;

import javax.inject.Inject;

import alektas.pocketbasket.domain.Repository;
import io.reactivex.Completable;

public class ResetShowcase implements UseCase<Boolean, Completable> {
    private Repository mRepository;

    @Inject
    public ResetShowcase(Repository repository) {
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
