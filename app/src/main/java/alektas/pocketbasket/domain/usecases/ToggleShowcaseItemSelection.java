package alektas.pocketbasket.domain.usecases;

import javax.inject.Inject;

import alektas.pocketbasket.data.db.entities.ShowcaseItem;
import alektas.pocketbasket.domain.Repository;

public class ToggleShowcaseItemSelection implements UseCase<ShowcaseItem, Void> {
    private Repository mRepository;

    @Inject
    public ToggleShowcaseItemSelection(Repository repository) {
        mRepository = repository;
    }

    @Override
    public Void execute(ShowcaseItem item) {
        mRepository.toggleDeletingSelection(item);
        return null;
    }
}
