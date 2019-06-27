package alektas.pocketbasket.domain.usecases;

import alektas.pocketbasket.domain.Repository;
import alektas.pocketbasket.domain.entities.ItemModel;

public class RemoveItemFromBasket implements UseCase<String, Boolean> {
    private Repository mRepository;
    private boolean byName;

    public RemoveItemFromBasket(Repository repository) {
        mRepository = repository;
    }

    public RemoveItemFromBasket(Repository repository, boolean byName) {
        mRepository = repository;
        this.byName = byName;
    }

    @Override
    public void execute(String s, Callback<Boolean> callback) {
        if (byName) {
            ItemModel item = mRepository.getItemByName(s);
            if (item == null) return;
            s = item.getKey();
        }
        mRepository.removeFromBasket(s, callback);
    }
}
