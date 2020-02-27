package alektas.pocketbasket.domain.usecases;

import alektas.pocketbasket.domain.Repository;

public class ResetItemsUseCase implements UseCase<Boolean, Boolean> {
    private Repository mRepository;

    public ResetItemsUseCase(Repository repository) {
        mRepository = repository;
    }

    @Override
    public void execute(Boolean fullReset, Callback<Boolean> callback) {
        if (fullReset) {
            mRepository.resetShowcase(callback);
        } else {
            mRepository.returnDeletedItems(callback);
        }
    }
}
