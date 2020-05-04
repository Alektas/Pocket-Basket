package alektas.pocketbasket.domain.usecases;

import javax.inject.Inject;

import alektas.pocketbasket.domain.Repository;
import io.reactivex.Observable;

public class GetViewMode implements UseCase<Void, Observable<Boolean>> {
    private Repository mRepository;

    @Inject
    public GetViewMode(Repository repository) {
        mRepository = repository;
    }

    @Override
    public Observable<Boolean> execute(Void request) {
        return mRepository.observeViewMode();
    }

}
