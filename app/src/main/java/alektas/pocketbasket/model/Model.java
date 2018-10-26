package alektas.pocketbasket.model;

import androidx.lifecycle.LiveData;

import java.util.List;

import alektas.pocketbasket.db.entity.Item;

public interface Model {
    Item getBasketItem(String key);
    void addBasketItem(Item item);
    void changeItemState(Item item);
    void checkAll(boolean state);
    void removeBasketItem(Item item);
    void clearBasket();

    void insertItem(Item item);
    void deleteItems(List<Item> item);
    void setFilter(int tag);
    void resetShowcase(boolean fullReset);

    LiveData<List<Item>> getAllData();
    LiveData<List<Item>> getBasketData();
}
