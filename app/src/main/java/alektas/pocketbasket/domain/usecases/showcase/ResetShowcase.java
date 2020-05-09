package alektas.pocketbasket.domain.usecases.showcase;

import javax.inject.Inject;

import alektas.pocketbasket.domain.ShowcaseRepository;
import alektas.pocketbasket.domain.usecases.UseCase;
import io.reactivex.Completable;

public class ResetShowcase implements UseCase<Boolean, Completable> {
    private ShowcaseRepository mRepository;

    @Inject
    public ResetShowcase(ShowcaseRepository repository) {
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
