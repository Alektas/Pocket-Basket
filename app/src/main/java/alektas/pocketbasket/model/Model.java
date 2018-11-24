package alektas.pocketbasket.model;

import alektas.pocketbasket.db.entity.BasketMeta;
import androidx.lifecycle.LiveData;

import java.util.List;

import alektas.pocketbasket.db.entity.Item;

public interface Model {
    BasketMeta getBasketMeta(String key);
    List<BasketMeta> getBasketMeta();
    void putToBasket(String name);
    void moveItem(String name, int fromPosition, int toPosition);
    void checkItem(String name);
    boolean isChecked(String name);
    void checkAll(boolean state);
    void removeBasketItem(String name);
    void deleteChecked();

    void addNewItem(Item item);
    void deleteItems(List<Item> item);
    void setFilter(int tag);
    void resetShowcase(boolean fullReset);
    List<Item> getItems(int tag);

    LiveData<List<Item>> getAllData();
    LiveData<List<Item>> getBasketData();
}
