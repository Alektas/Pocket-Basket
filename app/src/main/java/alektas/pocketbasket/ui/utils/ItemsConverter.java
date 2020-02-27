package alektas.pocketbasket.ui.utils;

import java.util.ArrayList;
import java.util.List;

import alektas.pocketbasket.domain.entities.ItemModel;

public class ItemsConverter {
    public static List<ItemModel> convert(List<Object> objects) {
        List<ItemModel> items = new ArrayList<>(objects.size());
        for (Object o : objects) {
            if (o instanceof ItemModel) items.add((ItemModel) o);
        }
        return items;
    }
}
