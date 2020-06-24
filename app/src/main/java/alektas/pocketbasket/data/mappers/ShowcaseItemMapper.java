package alektas.pocketbasket.data.mappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import alektas.pocketbasket.data.db.models.ShowcaseItemDbo;
import alektas.pocketbasket.domain.entities.ShowcaseItem;

public class ShowcaseItemMapper {

    public List<ShowcaseItem> convert(List<ShowcaseItemDbo> value) {
        List<ShowcaseItem> items = new ArrayList<>();
        for (ShowcaseItemDbo i : value) {
            items.add(convert(i));
        }
        return items;
    }

    public ShowcaseItem convert(ShowcaseItemDbo value) {
        return new ShowcaseItem(
                value.getKey(),
                value.getName(),
                value.getImgRef(),
                value.isInBasket(),
                value.isDeleted()
        );
    }

    public List<String> getKeys(Collection<ShowcaseItem> items) {
        List<String> keys = new ArrayList<>();
        for (ShowcaseItem item : items) {
            keys.add(item.getKey());
        }
        return keys;
    }

}