package alektas.pocketbasket.domain.usecases;

import javax.inject.Inject;

import alektas.pocketbasket.domain.Repository;

public class MoveBasketItemToTop implements UseCase<String, Void> {
    private Repository mRepository;

    @Inject
    public MoveBasketItemToTop(Repository repository) {
        mRepository = repository;
    }

    @Override
    public Void execute(String itemKey) {
        if (itemKey != null) mRepository.updateBasketItemPosition(itemKey, 0);
        return null;
    }

}
