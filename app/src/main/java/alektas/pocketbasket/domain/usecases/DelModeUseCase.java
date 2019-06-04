package alektas.pocketbasket.domain.usecases;

import alektas.pocketbasket.domain.Repository;

public class DelModeUseCase implements UseCase<Boolean, Void> {
    private Repository mRepository;

    public DelModeUseCase(Repository repository) {
        mRepository = repository;
    }

    @Override
    public void execute(Boolean delMode, Callback<Void> callback) {
        mRepository.setDelMode(delMode);
    }
}
