package alektas.pocketbasket.data.mappers;

import java.util.ArrayList;
import java.util.List;

import alektas.pocketbasket.data.db.models.BasketItemDbo;
import alektas.pocketbasket.domain.entities.BasketItem;

public class BasketItemMapper {

    public List<BasketItem> convert(List<BasketItemDbo> value) {
        List<BasketItem> items = new ArrayList<>();
        for (BasketItemDbo i : value) {
            items.add(convert(i));
        }
        return items;
    }

    public BasketItem convert(BasketItemDbo value) {
        return new BasketItem(
                value.getKey(),
                value.getName(),
                value.getImgRef(),
                value.isChecked()
        );
    }

    public List<BasketItem> convertObjects(List<Object> objects) {
        List<BasketItem> items = new ArrayList<>();
        if (objects.isEmpty() || !(objects.get(0) instanceof BasketItem)) return items;

        for (Object i : objects) {
            items.add((BasketItem) i);
        }
        return items;
    }
}