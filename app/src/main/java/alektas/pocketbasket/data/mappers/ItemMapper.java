package alektas.pocketbasket.data.mappers;

import java.util.ArrayList;
import java.util.List;

import alektas.pocketbasket.data.db.models.ItemDbo;
import alektas.pocketbasket.domain.entities.Item;

public class ItemMapper {

    public List<Item> convert(List<ItemDbo> value) {
        List<Item> items = new ArrayList<>();
        for (ItemDbo i : value) {
            items.add(convert(i));
        }
        return items;
    }

    public Item convert(ItemDbo value) {
        return new Item(
                value.getKey(),
                value.getName(),
                value.getImgRef()
        );
    }

}