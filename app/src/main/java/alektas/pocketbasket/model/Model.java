package alektas.pocketbasket.model;

import android.arch.lifecycle.LiveData;

import java.util.List;

import alektas.pocketbasket.db.entity.Item;

public interface Model {
    void addBasketItem(Item item);
    void insertItem(Item item);
    void deleteBasketItem(String key);
    void changeItemState(String key);
    void clearBasket();
    void setFilter(int tag);
    Item getBasketItem(String key);
    LiveData<List<Item>> getAllItems();
    LiveData<List<Item>> getByTag(int tag);
    LiveData<List<Item>> getBasketItems();
}
