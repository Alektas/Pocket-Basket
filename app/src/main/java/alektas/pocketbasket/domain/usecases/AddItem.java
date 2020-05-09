package alektas.pocketbasket.domain.usecases;

import java.util.NoSuchElementException;

import javax.inject.Inject;

import alektas.pocketbasket.domain.BasketRepository;
import alektas.pocketbasket.domain.ShowcaseRepository;
import io.reactivex.Single;

/**
 * Add a new item to the Showcase and put it to the Basket.
 * If an item already exist in the Showcase, only put it to the Basket.
 * If an item already stored in the Basket, do nothing.
 * Item existing checked regardless of the name register.<br><br>
 * <p>
 * INPUT: item displayed name regardless of the register<br>
 * RESULT: result codes
 */
public class AddItem implements UseCase<String, Single<Integer>> {
    public static final int NEW_ITEM_ADDED = 0;
    public static final int EXISTING_ITEM_ADDED = 1;
    public static final int ERROR_INVALID_NAME = 601;
    public static final int ERROR_UNKNOWN = 666;

    private ShowcaseRepository mShowcaseRepository;
    private BasketRepository mBasketRepository;

    @Inject
    public AddItem(ShowcaseRepository showcaseRepository, BasketRepository basketRepository) {
        mShowcaseRepository = showcaseRepository;
        mBasketRepository = basketRepository;
    }

    @Override
    public Single<Integer> execute(String name) {
        if (name == null || name.isEmpty()) {
            return Single.just(ERROR_INVALID_NAME);
        }
        String capName = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        return mShowcaseRepository.getItemByName(capName)
                .flatMapSingle(item -> mBasketRepository.putToBasket(item.getKey())
                        .toSingleDefault(EXISTING_ITEM_ADDED)
                        .onErrorReturnItem(ERROR_UNKNOWN))
                .onErrorResumeNext(error -> {
                    if (error instanceof NoSuchElementException) {
                        return mShowcaseRepository.createItem(name)
                                .andThen(mBasketRepository.putToBasket(name))
                                .toSingleDefault(NEW_ITEM_ADDED)
                                .onErrorReturnItem(ERROR_UNKNOWN);
                    }
                    return Single.just(ERROR_UNKNOWN);
                });
    }
}
