package alektas.pocketbasket.domain;

import java.util.List;

import alektas.pocketbasket.domain.entities.BasketItemModel;
import alektas.pocketbasket.domain.entities.ItemModel;
import alektas.pocketbasket.domain.entities.ShowcaseItemModel;
import alektas.pocketbasket.domain.utils.Observable;

public interface Repository {

    /**
     * Data contains only items stored in the basket.
     */
    Observable<List<BasketItemModel>> getBasketData();
    boolean isItemInBasket(String name);
    void putToBasket(String name);
    void removeFromBasket(String name);
    void updatePositions(List<String> names);
    void markItem(String name);
    void markAll();
    void removeMarked();

    /**
     * Data contains only items consisted to the selected category.
     */
    Observable<List<ShowcaseItemModel>> getShowcaseData();
    void addNewItem(String name);
    void setFilter(String tag);
    void resetShowcase();
    void insertPredefinedItems();
    void updateAll();
    /**
     * Find item by name in all categories.
     * Case sensitive.
     *
     * @param name key of the item
     * @return item domain model or null
     */
    ItemModel getItem(String name);

    /**
     * Contains current mode state.
     * 'true' = showcase mode, 'false' = basket mode.
     */
    Observable<Boolean> showcaseModeData();
    void setShowcaseMode(boolean showcaseMode);

    /**
     * Contains 'true' if the delete mode is active.
     */
    Observable<Boolean> delModeData();
    void setDelMode(boolean delMode);

    void selectForDeleting(ShowcaseItemModel item);
    /**
     * Delete from the Showcase all items selected by user for the deleting.
     */
    void deleteSelectedItems();

}