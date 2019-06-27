package alektas.pocketbasket.domain.usecases;

import alektas.pocketbasket.domain.Repository;

public class CleanBasketUseCase implements UseCase<Void, Boolean> {
    private Repository mRepository;

    public CleanBasketUseCase(Repository repository) {
        mRepository = repository;
    }

    @Override
    public void execute(Void v, Callback<Boolean> callback) {
        mRepository.cleanBasket(callback);
    }
}
