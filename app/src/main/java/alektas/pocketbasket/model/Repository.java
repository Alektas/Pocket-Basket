package alektas.pocketbasket.model;

import alektas.pocketbasket.db.entity.BasketMeta;
import androidx.lifecycle.LiveData;

import java.util.List;

import alektas.pocketbasket.db.entity.Item;

public interface Repository {
    BasketMeta getItemMeta(String name);
    void putToBasket(String name);
    void removeFromBasket(String name);
    void checkItem(String name);
    boolean isChecked(String name);
    void updatePositions(List<Item> items);
    void checkAll();
    void deleteChecked();

    void addNewItem(Item item);
    void deleteItems(List<Item> item);
    void setFilter(int tag);
    void resetShowcase(boolean fullReset);
    List<Item> getItems(int tag);

    LiveData<List<Item>> getShowcaseData();
    LiveData<List<Item>> getBasketData();
}
