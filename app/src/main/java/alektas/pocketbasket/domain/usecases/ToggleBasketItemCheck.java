package alektas.pocketbasket.domain.usecases;

import javax.inject.Inject;

import alektas.pocketbasket.domain.Repository;

public class ToggleBasketItemCheck implements UseCase<String, Void> {
    private Repository mRepository;

    @Inject
    public ToggleBasketItemCheck(Repository repository) {
        mRepository = repository;
    }

    @Override
    public Void execute(String s) {
        mRepository.toggleBasketItemCheck(s);
        return null;
    }

}
