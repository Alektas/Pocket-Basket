package alektas.pocketbasket.domain.usecases;

import java.util.List;

import alektas.pocketbasket.domain.Repository;

public class MoveBasketItem implements UseCase<List<String>, Void> {
    private Repository mRepository;

    public MoveBasketItem(Repository repository) {
        mRepository = repository;
    }

    @Override
    public void execute(List<String> strings, Callback<Void> callback) {
        mRepository.updatePositions(strings);
    }
}
