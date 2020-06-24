package alektas.pocketbasket.domain.usecases.showcase;

import javax.inject.Inject;

import alektas.pocketbasket.domain.ShowcaseRepository;
import alektas.pocketbasket.domain.entities.ShowcaseItem;
import alektas.pocketbasket.domain.usecases.UseCase;

public class ToggleShowcaseItemSelection implements UseCase<ShowcaseItem, Void> {
    private ShowcaseRepository mRepository;

    @Inject
    public ToggleShowcaseItemSelection(ShowcaseRepository repository) {
        mRepository = repository;
    }

    @Override
    public Void execute(ShowcaseItem item) {
        mRepository.toggleDeletingSelection(item);
        return null;
    }
}
