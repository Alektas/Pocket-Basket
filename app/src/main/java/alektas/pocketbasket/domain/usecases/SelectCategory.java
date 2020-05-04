package alektas.pocketbasket.domain.usecases;

import javax.inject.Inject;

import alektas.pocketbasket.domain.Repository;

public class SelectCategory implements UseCase<String, Void> {
    private Repository mRepository;

    @Inject
    public SelectCategory(Repository repository) {
        mRepository = repository;
    }

    @Override
    public Void execute(String s) {
        mRepository.setCategory(s);
        return null;
    }
}
