package alektas.pocketbasket.domain.usecases;

import javax.inject.Inject;

import alektas.pocketbasket.domain.Repository;

public class SetViewMode implements UseCase<Boolean, Void> {
    private Repository mRepository;

    @Inject
    public SetViewMode(Repository repository) {
        mRepository = repository;
    }

    @Override
    public Void execute(Boolean isShowcaseMode) {
        mRepository.setViewMode(isShowcaseMode);
        return null;
    }

}
