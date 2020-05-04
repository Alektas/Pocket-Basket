package alektas.pocketbasket.domain;

import java.util.List;

import alektas.pocketbasket.data.db.entities.BasketItem;
import alektas.pocketbasket.data.db.entities.Item;
import alektas.pocketbasket.data.db.entities.ShowcaseItem;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

public interface Repository {

    /**
     * Data contains only items stored in the basket.
     */
    Observable<List<BasketItem>> getBasketData();
    Single<Boolean> isItemInBasket(String key);
    Completable putToBasket(String key);
    Completable removeFromBasket(String key);
    void updateBasketPositions(List<String> keys);
    void updateBasketItemPosition(String key, int position);
    /**
     * Change item state in "Basket"
     */
    void toggleBasketItemCheck(String key);
    /**
     * Check all items in Basket (or uncheck if already all items are checked)
     */
    void toggleBasketCheck();
    Completable removeCheckedBasketItems();
    Completable cleanBasket();

    /**
     * Data contains only items consisted to the selected category.
     */
    Observable<List<ShowcaseItem>> getShowcaseData();
    void addNewItem(String name);
    /**
     * Show in Showcase only items with specified tag
     * @param tag name of the category
     */
    void setCategory(String tag);
    /**
     * Restore items deleted by user from the showcase and delete user items.
     * If it's neccessary to only restore deleted items use {@link #restoreShowcase}.
     */
    Completable resetShowcase();
    /**
     * Restore items deleted by user from the showcase.
     * If it's neccessary also to remove user items use {@link #resetShowcase}.
     */
    Completable restoreShowcase();
    void updateDisplayedNames();
    /**
     * Find item by name in all categories.
     * Case sensitive.
     *
     * @param name displayed name of the item
     * @return item domain model or null
     */
    Maybe<Item> getItemByName(String name);

    /**
     * Contains current mode state.
     * 'true' = showcase mode, 'false' = basket mode.
     */
    Observable<Boolean> observeViewMode();
    void setViewMode(boolean showcaseMode);
    /**
     * Contains 'true' if the delete mode is active.
     */
    Observable<Boolean> observeDelMode();
    void setDelMode(boolean delMode);
    Observable<Integer> getDelItemsCountData();
    void toggleDeletingSelection(ShowcaseItem item);
    /**
     * Delete from the Showcase all items selected by user for the deleting.
     */
    Completable deleteSelectedItems();

}
