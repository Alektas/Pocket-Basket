package alektas.pocketbasket.domain.usecases;

import alektas.pocketbasket.domain.Repository;

public class SelectShowcaseItem implements UseCase<String, Boolean> {
    private Repository mRepository;

    public SelectShowcaseItem(Repository repository) {
        mRepository = repository;
    }

    @Override
    public void execute(String key, Callback<Boolean> callback) {
        if (mRepository.isItemInBasket(key)) {
            mRepository.removeFromBasket(key);
            callback.onResponse(false);
        } else {
            mRepository.putToBasket(key);
            callback.onResponse(true);
        }
    }
}
