package alektas.pocketbasket.data;

import java.util.List;

import alektas.pocketbasket.db.entities.BasketMeta;
import alektas.pocketbasket.db.entities.Item;
import androidx.lifecycle.LiveData;

public interface Repository {
    BasketMeta getItemMeta(String name);
    void putToBasket(String name);
    void removeFromBasket(String name);
    void checkItem(String name);
    boolean isChecked(String name);
    void updatePositions(List<Item> items);
    void checkAll();
    void deleteChecked();
    List<Item> getBasketItems();

    void addNewItem(Item item);
    void deleteItems(List<Item> item);
    void setFilter(String tag);
    void resetShowcase();
    void insertAll(List<Item> items);
    void updateAll();
    List<Item> getItems();
    List<Item> getItems(String tag);

    LiveData<List<Item>> getShowcaseData();
    LiveData<List<Item>> getBasketData();
}
