package alektas.pocketbasket.domain;

import java.util.List;

import alektas.pocketbasket.domain.entities.ItemModel;
import alektas.pocketbasket.domain.utils.Observable;

public interface Repository {

    Observable<List<? extends ItemModel>> getBasketData();
    boolean isItemInBasket(String name);
    void putToBasket(String name);
    void removeFromBasket(String name);
    void updatePositions(List<String> names);
    boolean isChecked(String name);
    void checkItem(String name);
    void checkAll();
    void deleteChecked();

    Observable<List<? extends ItemModel>> getShowcaseData();
    void addNewItem(String name);
    /**
     * Delete from the Showcase all items presented in the list
     * @param items deleting items
     */
    void deleteItems(List<? extends ItemModel> items);
    void setFilter(String tag);
    void resetShowcase();
    void insertAll(List<? extends ItemModel> items);
    void updateAll();
    ItemModel getItem(String name);

    Observable<Boolean> showcaseModeState();
    void setShowcaseMode(boolean showcaseMode);

    Observable<Boolean> delModeState();
    void setDelMode(boolean delMode);

}
