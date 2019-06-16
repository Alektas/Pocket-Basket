package alektas.pocketbasket.domain.usecases;

import alektas.pocketbasket.domain.Repository;

public class ResetItemsUseCase implements UseCase<Boolean, Void> {
    private Repository mRepository;

    public ResetItemsUseCase(Repository repository) {
        mRepository = repository;
    }

    @Override
    public void execute(Boolean fullReset, Callback<Void> callback) {
        if (fullReset) {
            mRepository.resetShowcase();
        } else {
            mRepository.returnDeletedItems();
        }
    }
}
