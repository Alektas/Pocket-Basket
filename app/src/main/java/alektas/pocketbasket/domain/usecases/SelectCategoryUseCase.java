package alektas.pocketbasket.domain.usecases;

import alektas.pocketbasket.domain.Repository;

public class SelectCategoryUseCase implements UseCase<String, Void> {
    private Repository mRepository;

    public SelectCategoryUseCase(Repository repository) {
        mRepository = repository;
    }

    @Override
    public void execute(String s, Callback<Void> callback) {
        mRepository.setFilter(s);
    }
}
