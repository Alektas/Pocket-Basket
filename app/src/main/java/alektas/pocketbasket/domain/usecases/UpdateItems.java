package alektas.pocketbasket.domain.usecases;

import javax.inject.Inject;

import alektas.pocketbasket.domain.Repository;

public class UpdateItems implements UseCase<Void, Void> {
    private Repository mRepository;

    @Inject
    public UpdateItems(Repository repository) {
        mRepository = repository;
    }

    @Override
    public Void execute(Void v) {
        mRepository.updateDisplayedNames();
        return null;
    }
}
