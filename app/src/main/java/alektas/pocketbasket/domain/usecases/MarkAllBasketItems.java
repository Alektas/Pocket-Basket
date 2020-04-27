package alektas.pocketbasket.domain.usecases;

import alektas.pocketbasket.domain.Repository;

/**
 * Mark all items in the basket, or uncheck all if they are already marked.
 */
public class MarkAllBasketItems implements UseCase<Void, Void> {
    private Repository mRepository;

    public MarkAllBasketItems(Repository repository) {
        mRepository = repository;
    }

    @Override
    public Void execute(Void v) {
        mRepository.toggleBasketCheck();
        return null;
    }

}
