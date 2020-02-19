package alektas.pocketbasket.domain.usecases;

import alektas.pocketbasket.domain.Repository;
import alektas.pocketbasket.domain.entities.ItemModel;

/**
 * Add a new item to the Showcase and put it to the Basket.
 * If an item already exist in the Showcase, only put it to the Basket.
 * If an item already stored in the Basket, do nothing.
 * Item existing checked regardless of the name register.
 */
public class AddItemUseCase implements UseCase<String, Integer> {
    public static final int NEW_ITEM_ADDED = 0;
    public static final int EXISTING_ITEM_ADDED = 1;
    public static final int INVALID_NAME = 666;

    private Repository mRepository;

    public AddItemUseCase(Repository repository) {
        mRepository = repository;
    }

    /**
     * @param name item displayed name regardless of the register
     * @param callback returns result of the execution (see result codes at this class fields)
     */
    @Override
    public void execute(String name, Callback<Integer> callback) {
        if (name == null || name.isEmpty()) {
            callback.onResponse(INVALID_NAME);
            return;
        }
        String capName = name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
        ItemModel item = mRepository.getItemByName(capName);
        if (item != null) {
            mRepository.putToBasket(item.getKey());
            callback.onResponse(EXISTING_ITEM_ADDED);
        } else {
            mRepository.addNewItem(name);
            callback.onResponse(NEW_ITEM_ADDED);
        }
    }
}
