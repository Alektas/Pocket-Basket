package alektas.pocketbasket.domain.usecases.showcase;

import javax.inject.Inject;

import alektas.pocketbasket.domain.ShowcaseRepository;
import alektas.pocketbasket.domain.usecases.UseCase;

public class UpdateItems implements UseCase<Void, Void> {
    private ShowcaseRepository mRepository;

    @Inject
    public UpdateItems(ShowcaseRepository repository) {
        mRepository = repository;
    }

    @Override
    public Void execute(Void v) {
        mRepository.updateDisplayedNames();
        return null;
    }
}
