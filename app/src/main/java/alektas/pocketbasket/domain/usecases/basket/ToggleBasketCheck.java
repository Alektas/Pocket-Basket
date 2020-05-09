package alektas.pocketbasket.domain.usecases.basket;

import javax.inject.Inject;

import alektas.pocketbasket.domain.BasketRepository;
import alektas.pocketbasket.domain.usecases.UseCase;

/**
 * Check all items in the basket, or uncheck all if they are already checked.
 */
public class ToggleBasketCheck implements UseCase<Void, Void> {
    private BasketRepository mRepository;

    @Inject
    public ToggleBasketCheck(BasketRepository repository) {
        mRepository = repository;
    }

    @Override
    public Void execute(Void v) {
        mRepository.toggleBasketCheck();
        return null;
    }

}
