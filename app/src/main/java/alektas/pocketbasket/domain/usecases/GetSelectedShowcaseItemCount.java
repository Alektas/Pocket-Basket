package alektas.pocketbasket.domain.usecases;

import javax.inject.Inject;

import alektas.pocketbasket.domain.Repository;
import io.reactivex.Observable;

public class GetSelectedShowcaseItemCount implements UseCase<Void, Observable<Integer>> {
    private Repository mRepository;

    @Inject
    public GetSelectedShowcaseItemCount(Repository repository) {
        mRepository = repository;
    }

    @Override
    public Observable<Integer> execute(Void request) {
        return mRepository.getDelItemsCountData();
    }

}
