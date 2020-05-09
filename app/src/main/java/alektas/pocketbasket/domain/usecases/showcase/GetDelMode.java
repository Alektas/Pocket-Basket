package alektas.pocketbasket.domain.usecases.showcase;

import javax.inject.Inject;

import alektas.pocketbasket.domain.ShowcaseRepository;
import alektas.pocketbasket.domain.usecases.UseCase;
import io.reactivex.Observable;

public class GetDelMode implements UseCase<Void, Observable<Boolean>> {
    private ShowcaseRepository mRepository;

    @Inject
    public GetDelMode(ShowcaseRepository repository) {
        mRepository = repository;
    }

    @Override
    public Observable<Boolean> execute(Void request) {
        return mRepository.observeDelMode();
    }

}
