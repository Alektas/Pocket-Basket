package alektas.pocketbasket.domain.usecases.showcase;

import javax.inject.Inject;

import alektas.pocketbasket.domain.ShowcaseRepository;
import alektas.pocketbasket.domain.usecases.UseCase;

public class SetDelMode implements UseCase<Boolean, Void> {
    private ShowcaseRepository mRepository;

    @Inject
    public SetDelMode(ShowcaseRepository repository) {
        mRepository = repository;
    }

    @Override
    public Void execute(Boolean delMode) {
        mRepository.setDelMode(delMode);
        return null;
    }

}
