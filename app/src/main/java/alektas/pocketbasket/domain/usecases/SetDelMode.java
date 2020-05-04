package alektas.pocketbasket.domain.usecases;

import javax.inject.Inject;

import alektas.pocketbasket.domain.Repository;

public class SetDelMode implements UseCase<Boolean, Void> {
    private Repository mRepository;

    @Inject
    public SetDelMode(Repository repository) {
        mRepository = repository;
    }

    @Override
    public Void execute(Boolean delMode) {
        mRepository.setDelMode(delMode);
        return null;
    }

}
