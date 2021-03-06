package alektas.pocketbasket.domain.usecases;

import alektas.pocketbasket.domain.Repository;

/**
 * Delete all marked items in the Basket.
 */
public class RemoveMarkedItems implements UseCase<Void, Boolean> {
    private Repository mRepository;

    public RemoveMarkedItems(Repository repository) {
        mRepository = repository;
    }

    @Override
    public void execute(Void v, Callback<Boolean> callback) {
        mRepository.removeMarked(callback);
    }
}
