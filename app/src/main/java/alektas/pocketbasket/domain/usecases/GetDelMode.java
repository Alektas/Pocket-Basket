package alektas.pocketbasket.domain.usecases;

import javax.inject.Inject;

import alektas.pocketbasket.domain.Repository;
import io.reactivex.Observable;

public class GetDelMode implements UseCase<Void, Observable<Boolean>> {
    private Repository mRepository;

    @Inject
    public GetDelMode(Repository repository) {
        mRepository = repository;
    }

    @Override
    public Observable<Boolean> execute(Void request) {
        return mRepository.observeDelMode();
    }

}
