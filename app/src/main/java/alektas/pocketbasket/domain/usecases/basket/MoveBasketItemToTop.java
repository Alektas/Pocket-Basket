package alektas.pocketbasket.domain.usecases.basket;

import javax.inject.Inject;

import alektas.pocketbasket.domain.BasketRepository;
import alektas.pocketbasket.domain.usecases.UseCase;

public class MoveBasketItemToTop implements UseCase<String, Void> {
    private BasketRepository mRepository;

    @Inject
    public MoveBasketItemToTop(BasketRepository repository) {
        mRepository = repository;
    }

    @Override
    public Void execute(String itemKey) {
        if (itemKey != null) mRepository.updateBasketItemPosition(itemKey, 0);
        return null;
    }

}
