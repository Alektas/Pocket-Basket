package alektas.pocketbasket.domain.usecases;

import alektas.pocketbasket.domain.Repository;

public class SelectShowcaseItem implements UseCase<String, Boolean> {
    private Repository mRepository;

    public SelectShowcaseItem(Repository repository) {
        mRepository = repository;
    }

    @Override
    public void execute(String name, Callback<Boolean> callback) {
        if (mRepository.isItemInBasket(name)) {
            mRepository.removeFromBasket(name);
            callback.onResponse(false);
        } else {
            mRepository.putToBasket(name);
            callback.onResponse(true);
        }
    }
}
