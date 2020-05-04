package alektas.pocketbasket.domain.usecases;

import javax.inject.Inject;

import alektas.pocketbasket.domain.Repository;
import io.reactivex.Completable;

public class DeleteSelectedShowcaseItems implements UseCase<Void, Completable> {
    private Repository mRepository;

    @Inject
    public DeleteSelectedShowcaseItems(Repository repository) {
        mRepository = repository;
    }

    @Override
    public Completable execute(Void request) {
        return mRepository.deleteSelectedItems();
    }

}
