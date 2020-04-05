package alektas.pocketbasket.domain;

import java.util.List;

import alektas.pocketbasket.domain.entities.BasketItemModel;
import alektas.pocketbasket.domain.entities.ItemModel;
import alektas.pocketbasket.domain.entities.ShowcaseItemModel;
import alektas.pocketbasket.domain.usecases.UseCase;
import alektas.pocketbasket.domain.utils.Observable;

public interface Repository {

    /**
     * Data contains only items stored in the basket.
     */
    Observable<List<BasketItemModel>> getBasketData();
    boolean isItemInBasket(String key);
    void putToBasket(String key);
    void removeFromBasket(String key);
    void removeFromBasket(String key, UseCase.Callback<Boolean> callback);
    void updatePositions(List<String> keys);
    void updatePosition(String key, int position);
    void markItem(String key);
    void markAll();
    void removeMarked(UseCase.Callback<Boolean> callback);
    void cleanBasket(UseCase.Callback<Boolean> callback);

    /**
     * Data contains only items consisted to the selected category.
     */
    Observable<List<ShowcaseItemModel>> getShowcaseData();
    void addNewItem(String name);
    void setFilter(String tag);
    void resetShowcase(UseCase.Callback<Boolean> callback);
    void returnDeletedItems(UseCase.Callback<Boolean> callback);
    void updateNames();
    /**
     * Find item by name in all categories.
     * Case sensitive.
     *
     * @param name key of the item
     * @return item domain model or null
     */
    ItemModel getItemByName(String name);

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

    Observable<Integer> getDelItemsCountData();

    void selectForDeleting(ShowcaseItemModel item);
    /**
     * Delete from the Showcase all items selected by user for the deleting.
     */
    void deleteSelectedItems();

}
