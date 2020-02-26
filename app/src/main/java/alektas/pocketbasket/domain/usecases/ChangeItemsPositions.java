package alektas.pocketbasket.domain.usecases;

import java.util.ArrayList;
import java.util.List;

import alektas.pocketbasket.domain.Repository;
import alektas.pocketbasket.domain.entities.ItemModel;

public class ChangeItemsPositions implements UseCase<List<ItemModel>, Void> {
    private Repository mRepository;

    public ChangeItemsPositions(Repository repository) {
        mRepository = repository;
    }

    @Override
    public void execute(List<ItemModel> items, Callback<Void> callback) {
        if (items == null || items.isEmpty()) return;
        List<String> keys = new ArrayList<>();
        for (ItemModel item : items) {
            keys.add(item.getKey());
        }
        mRepository.updatePositions(keys);
    }
}
