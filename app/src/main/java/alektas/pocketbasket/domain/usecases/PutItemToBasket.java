package alektas.pocketbasket.domain.usecases;

import alektas.pocketbasket.domain.Repository;

public class PutItemToBasket implements UseCase<String, Boolean> {
    private Repository mRepository;

    public PutItemToBasket(Repository repository) {
        mRepository = repository;
    }

    @Override
    public void execute(String name, Callback<Boolean> callback) {
        mRepository.putToBasket(name);
        callback.onResponse(true);
    }
}
