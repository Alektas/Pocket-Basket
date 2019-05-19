package alektas.pocketbasket.domain.usecases;

import alektas.pocketbasket.domain.Repository;

public class MarkBasketItem implements UseCase<String, Void> {
    private Repository mRepository;

    public MarkBasketItem(Repository repository) {
        mRepository = repository;
    }

    @Override
    public void execute(String s, Callback<Void> callback) {
        mRepository.markItem(s);
    }
}
