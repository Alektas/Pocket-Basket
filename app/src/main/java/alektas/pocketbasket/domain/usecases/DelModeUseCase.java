package alektas.pocketbasket.domain.usecases;

import alektas.pocketbasket.domain.Repository;

public class DelModeUseCase implements UseCase<Boolean, Void> {
    private Repository mRepository;

    public DelModeUseCase(Repository repository) {
        mRepository = repository;
    }

    @Override
    public Void execute(Boolean delMode) {
        mRepository.setDelMode(delMode);
        return null;
    }

}
