package alektas.pocketbasket.domain.usecases.showcase;

import javax.inject.Inject;

import alektas.pocketbasket.domain.BasketRepository;
import alektas.pocketbasket.domain.usecases.UseCase;
import io.reactivex.Single;

public class SelectShowcaseItem implements UseCase<String, Single<Integer>> {
    private BasketRepository mRepository;
    public static final int ITEM_REMOVED_FROM_BASKET = 0;
    public static final int ITEM_ADDED_TO_BASKET = 1;

    @Inject
    public SelectShowcaseItem(BasketRepository repository) {
        mRepository = repository;
    }

    @Override
    public Single<Integer> execute(String key) {
        return mRepository.isItemInBasket(key)
                .flatMap(isInBasket -> {
                    if (isInBasket) {
                        return mRepository.removeFromBasket(key)
                                .toSingleDefault(ITEM_REMOVED_FROM_BASKET);
                    } else {
                        return mRepository.putToBasket(key)
                                .toSingleDefault(ITEM_ADDED_TO_BASKET);
                    }
                });
    }

}
