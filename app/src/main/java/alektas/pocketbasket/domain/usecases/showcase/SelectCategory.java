package alektas.pocketbasket.domain.usecases.showcase;

import javax.inject.Inject;

import alektas.pocketbasket.domain.ShowcaseRepository;
import alektas.pocketbasket.domain.usecases.UseCase;

public class SelectCategory implements UseCase<String, Void> {
    private ShowcaseRepository mRepository;

    @Inject
    public SelectCategory(ShowcaseRepository repository) {
        mRepository = repository;
    }

    @Override
    public Void execute(String s) {
        mRepository.setCategory(s);
        return null;
    }
}
