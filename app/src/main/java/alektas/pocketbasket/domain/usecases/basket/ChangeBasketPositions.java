package alektas.pocketbasket.domain.usecases.basket;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import alektas.pocketbasket.domain.BasketRepository;
import alektas.pocketbasket.domain.entities.ItemModel;
import alektas.pocketbasket.domain.usecases.UseCase;

public class ChangeBasketPositions implements UseCase<List<ItemModel>, Void> {
    private BasketRepository mRepository;

    @Inject
    public ChangeBasketPositions(BasketRepository repository) {
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
