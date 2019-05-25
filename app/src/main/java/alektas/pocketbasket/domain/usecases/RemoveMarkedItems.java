package alektas.pocketbasket.domain.usecases;

import alektas.pocketbasket.domain.Repository;

public class RemoveMarkedItems implements UseCase<Void, Void> {
    private Repository mRepository;

    public RemoveMarkedItems(Repository repository) {
        mRepository = repository;
    }

    @Override
    public void execute(Void v, Callback<Void> callback) {
        mRepository.removeMarked();
    }
}
