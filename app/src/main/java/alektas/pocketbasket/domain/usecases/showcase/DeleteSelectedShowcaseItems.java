package alektas.pocketbasket.domain.usecases.showcase;

import java.util.ArrayList;
import java.util.HashSet;

import javax.inject.Inject;

import alektas.pocketbasket.domain.BasketRepository;
import alektas.pocketbasket.domain.ShowcaseRepository;
import alektas.pocketbasket.domain.usecases.UseCase;
import io.reactivex.Completable;

public class DeleteSelectedShowcaseItems implements UseCase<Void, Completable> {
    private ShowcaseRepository mShowcaseRepository;
    private BasketRepository mBasketRepository;

    @Inject
    public DeleteSelectedShowcaseItems(ShowcaseRepository showcaseRepository, BasketRepository basketRepository) {
        mShowcaseRepository = showcaseRepository;
        mBasketRepository = basketRepository;
    }

    @Override
    public Completable execute(Void request) {
        ArrayList<Completable> actions = new ArrayList<>();
        actions.add(mShowcaseRepository.deleteSelectedShowcaseItems());
        actions.add(mShowcaseRepository.getSelectedItemsKeys()
                .first(new HashSet<>())
                .flatMapCompletable(keys -> mBasketRepository.removeItems(keys)));
        return Completable.merge(actions);
    }

}
