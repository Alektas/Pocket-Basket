package alektas.pocketbasket.domain.usecases;

import javax.inject.Inject;

import alektas.pocketbasket.domain.Repository;

/**
 * Check all items in the basket, or uncheck all if they are already checked.
 */
public class ToggleBasketCheck implements UseCase<Void, Void> {
    private Repository mRepository;

    @Inject
    public ToggleBasketCheck(Repository repository) {
        mRepository = repository;
    }

    @Override
    public Void execute(Void v) {
        mRepository.toggleBasketCheck();
        return null;
    }

}
