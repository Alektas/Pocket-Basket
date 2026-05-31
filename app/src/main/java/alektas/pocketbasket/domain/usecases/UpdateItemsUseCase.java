package alektas.pocketbasket.domain.usecases;

import alektas.pocketbasket.domain.Repository;

public class UpdateItemsUseCase implements UseCase<Void, Boolean> {
    private Repository mRepository;

    public UpdateItemsUseCase(Repository repository) {
        mRepository = repository;
    }

    @Override
    public void execute(Void v, Callback<Boolean> callback) {
        mRepository.updateNames(callback);
    }
}
