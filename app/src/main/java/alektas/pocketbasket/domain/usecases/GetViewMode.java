package alektas.pocketbasket.domain.usecases;

import javax.inject.Inject;

import alektas.pocketbasket.domain.AppRepository;
import io.reactivex.Observable;

public class GetViewMode implements UseCase<Void, Observable<Boolean>> {
    private AppRepository mRepository;

    @Inject
    public GetViewMode(AppRepository repository) {
        mRepository = repository;
    }

    @Override
    public Observable<Boolean> execute(Void request) {
        return mRepository.observeViewMode();
    }

}
