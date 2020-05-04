package alektas.pocketbasket.domain.usecases;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import alektas.pocketbasket.domain.Repository;
import alektas.pocketbasket.domain.entities.ItemModel;

public class ChangeBasketPositions implements UseCase<List<ItemModel>, Void> {
    private Repository mRepository;

    @Inject
    public ChangeBasketPositions(Repository repository) {
        mRepository = repository;
    }

    @Override
    public Void execute(List<ItemModel> items) {
        if (items == null || items.isEmpty()) return null;
        List<String> keys = new ArrayList<>();
        for (ItemModel item : items) {
            keys.add(item.getKey());
        }
        mRepository.updateBasketPositions(keys);
        return null;
    }

}
