package alektas.pocketbasket.domain.usecases;

import java.util.NoSuchElementException;

import javax.inject.Inject;

import alektas.pocketbasket.domain.Repository;
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

    private Repository mRepository;

    @Inject
    public AddItem(Repository repository) {
        mRepository = repository;
    }

    @Override
    public Single<Integer> execute(String name) {
        if (name == null || name.isEmpty()) {
            return Single.just(ERROR_INVALID_NAME);
        }
        String capName = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        return mRepository.getItemByName(capName)
                .flatMapSingle(item -> {
                    mRepository.putToBasket(item.getKey());
                    return Single.just(EXISTING_ITEM_ADDED);
                })
                .onErrorReturn(error -> {
                    if (error instanceof NoSuchElementException) {
                        mRepository.createItem(name);
                        return NEW_ITEM_ADDED;
                    }
                    return ERROR_UNKNOWN;
                });
    }
}
