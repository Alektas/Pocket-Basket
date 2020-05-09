package alektas.pocketbasket.domain.usecases.basket;

import javax.inject.Inject;

import alektas.pocketbasket.domain.BasketRepository;
import alektas.pocketbasket.domain.ShowcaseRepository;
import alektas.pocketbasket.domain.usecases.UseCase;
import io.reactivex.Completable;

public class RemoveBasketItem implements UseCase<String, Completable> {
    private BasketRepository mBasketRepository;
    private ShowcaseRepository mShowcaseRepository;
    private boolean byName;

    @Inject
    public RemoveBasketItem(
            BasketRepository basketRepository,
            ShowcaseRepository showcaseRepository,
            boolean byName
    ) {
        mBasketRepository = basketRepository;
        mShowcaseRepository = showcaseRepository;
        this.byName = byName;
    }

    @Override
    public Completable execute(String s) {
        if (byName) {
            return mShowcaseRepository.getItemByName(s)
                    .flatMapCompletable(item -> mBasketRepository.removeFromBasket(item.getKey()));
        }
        return mBasketRepository.removeFromBasket(s);
    }
}
