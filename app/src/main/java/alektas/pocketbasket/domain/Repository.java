package alektas.pocketbasket.domain;

import java.util.List;

import alektas.pocketbasket.db.entities.Item;
import alektas.pocketbasket.domain.entities.ItemModel;
import alektas.pocketbasket.domain.utils.Observable;

public interface Repository {

    Observable<List<Item>> getBasketData();
    boolean isItemInBasket(String name);
    void putToBasket(String name);
    void removeFromBasket(String name);
    void updatePositions(List<String> names);
    boolean isChecked(String name);
    void checkItem(String name);
    void checkAll();
    void deleteChecked();

    Observable<List<Item>> getShowcaseData();
    void addNewItem(String name);
    void deleteItems(List<Item> item);
    void setFilter(String tag);
    void resetShowcase();
    void insertAll(List<Item> items);
    void updateAll();
    ItemModel getItem(String name);

    Observable<Boolean> showcaseModeState();
    void setShowcaseMode(boolean showcaseMode);

    Observable<Boolean> delModeState();
    void setDelMode(boolean delMode);

}
