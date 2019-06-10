package alektas.pocketbasket.domain.usecases;

import alektas.pocketbasket.domain.Repository;

public class MarkAllBasketItems implements UseCase<Void, Void> {
    private Repository mRepository;

    public MarkAllBasketItems(Repository repository) {
        mRepository = repository;
    }

    @Override
    public void execute(Void v, Callback<Void> callback) {
        mRepository.markAll();
    }
}
