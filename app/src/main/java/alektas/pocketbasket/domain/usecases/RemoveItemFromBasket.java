package alektas.pocketbasket.domain.usecases;

import alektas.pocketbasket.domain.Repository;

public class RemoveItemFromBasket implements UseCase<String, Void> {
    private Repository mRepository;

    public RemoveItemFromBasket(Repository repository) {
        mRepository = repository;
    }

    @Override
    public void execute(String s, Callback<Void> callback) {
        mRepository.removeFromBasket(s);
    }
}
