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
        List<String> names = new ArrayList<>();
        for (ItemModel item : items) {
            names.add(item.getName());
        }
        mRepository.updatePositions(names);
    }
}
