package alektas.pocketbasket.domain.usecases;

import alektas.pocketbasket.domain.Repository;

public class PutItemToBasket implements UseCase<String, Boolean> {
    private Repository mRepository;

    public PutItemToBasket(Repository repository) {
        mRepository = repository;
    }

    @Override
    public void execute(String key, Callback<Boolean> callback) {
        mRepository.putToBasket(key);
        callback.onResponse(true);
    }
}
