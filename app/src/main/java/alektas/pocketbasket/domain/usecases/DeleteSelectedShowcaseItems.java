package alektas.pocketbasket.domain.usecases;

import javax.inject.Inject;

import alektas.pocketbasket.domain.Repository;

public class DeleteSelectedShowcaseItems implements UseCase<Void, Void> {
    private Repository mRepository;

    @Inject
    public DeleteSelectedShowcaseItems(Repository repository) {
        mRepository = repository;
    }

    @Override
    public Void execute(Void request) {
        mRepository.deleteSelectedItems();
        return null;
    }

}
