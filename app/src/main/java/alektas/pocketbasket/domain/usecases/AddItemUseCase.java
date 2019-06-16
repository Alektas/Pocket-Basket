package alektas.pocketbasket.domain.usecases;

import alektas.pocketbasket.domain.Repository;
import alektas.pocketbasket.domain.entities.ItemModel;

/**
 * Add a new item to the Showcase and put it to the Basket.
 * If an item already exist in the Showcase, only put it to the Basket.
 * If an item already stored in the Basket, do nothing.
 * Item existing checked regardless of the name register.
 */
public class AddItemUseCase implements UseCase<String, Boolean> {
    private Repository mRepository;

    public AddItemUseCase(Repository repository) {
        mRepository = repository;
    }

    @Override
    public void execute(String name, Callback<Boolean> callback) {
        if (name == null) return;
        // TODO: do the item find regardless of the name register
        String capName = name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
        ItemModel item = mRepository.getItemByName(capName);
        if (item != null) {
            mRepository.putToBasket(item.getKey());
        } else {
            mRepository.addNewItem(name);
            callback.onResponse(true);
        }
    }
}
