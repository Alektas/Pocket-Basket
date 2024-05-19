package alektas.pocketbasket.domain.usecases;

import alektas.pocketbasket.domain.Repository;

public class MoveItemToTopUseCase implements UseCase<String, Void> {
    private Repository mRepository;

    public MoveItemToTopUseCase(Repository repository) {
        mRepository = repository;
    }

    @Override
    public void execute(String itemKey, Callback<Void> callback) {
        if (itemKey == null) return;

        mRepository.updatePosition(itemKey, 0);
    }
}
