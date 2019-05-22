package alektas.pocketbasket.domain;

import java.util.List;

import alektas.pocketbasket.domain.entities.BasketItemModel;
import alektas.pocketbasket.domain.entities.ItemModel;
import alektas.pocketbasket.domain.entities.ShowcaseItemModel;
import alektas.pocketbasket.domain.utils.Observable;

public interface Repository {

    Observable<List<BasketItemModel>> getBasketData();
    boolean isItemInBasket(String name);
    void putToBasket(String name);
    void removeFromBasket(String name);
    void updatePositions(List<String> names);
    void markItem(String name);
    void markAll();
    void removeMarked();

    Observable<List<ShowcaseItemModel>> getShowcaseData();
    void addNewItem(String name);
    void setFilter(String tag);
    void resetShowcase();
    void insertPredefinedItems();
    void updateAll();
    ItemModel getItem(String name);

    Observable<Boolean> showcaseModeState();
    void setShowcaseMode(boolean showcaseMode);

    Observable<Boolean> delModeState();
    void setDelMode(boolean delMode);

    void selectForDeleting(ShowcaseItemModel item);
    /**
     * Delete from the Showcase all items selected by user for the deleting.
     */
    void deleteSelectedItems();

}
