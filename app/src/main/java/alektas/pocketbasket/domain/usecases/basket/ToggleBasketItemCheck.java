package alektas.pocketbasket.domain.usecases.basket;

import javax.inject.Inject;

import alektas.pocketbasket.domain.BasketRepository;
import alektas.pocketbasket.domain.usecases.UseCase;

public class ToggleBasketItemCheck implements UseCase<String, Void> {
    private BasketRepository mRepository;

    @Inject
    public ToggleBasketItemCheck(BasketRepository repository) {
        mRepository = repository;
    }

    @Override
    public Void execute(String s) {
        mRepository.toggleBasketItemCheck(s);
        return null;
    }

}
