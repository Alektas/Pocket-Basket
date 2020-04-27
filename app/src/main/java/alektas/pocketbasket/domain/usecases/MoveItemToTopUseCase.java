package alektas.pocketbasket.domain.usecases;

import alektas.pocketbasket.domain.Repository;

public class MoveItemToTopUseCase implements UseCase<String, Void> {
    private Repository mRepository;

    public MoveItemToTopUseCase(Repository repository) {
        mRepository = repository;
    }

    @Override
    public Void execute(String itemKey) {
        if (itemKey != null) mRepository.updateBasketItemPosition(itemKey, 0);
        return null;
    }

}
