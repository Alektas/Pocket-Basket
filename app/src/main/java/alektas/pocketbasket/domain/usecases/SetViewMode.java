package alektas.pocketbasket.domain.usecases;

import javax.inject.Inject;

import alektas.pocketbasket.domain.AppRepository;

public class SetViewMode implements UseCase<Boolean, Void> {
    private AppRepository mRepository;

    @Inject
    public SetViewMode(AppRepository repository) {
        mRepository = repository;
    }

    @Override
    public Void execute(Boolean isShowcaseMode) {
        mRepository.setViewMode(isShowcaseMode);
        return null;
    }

}
