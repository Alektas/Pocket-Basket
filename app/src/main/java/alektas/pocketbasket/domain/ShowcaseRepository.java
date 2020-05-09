package alektas.pocketbasket.domain;

import java.util.List;
import java.util.Set;

import alektas.pocketbasket.data.db.entities.Item;
import alektas.pocketbasket.data.db.entities.ShowcaseItem;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;

public interface ShowcaseRepository {

    /**
     * Data contains only items consisted to the selected category.
     */
    Observable<List<ShowcaseItem>> getShowcaseData();
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

    /**
     * Contains 'true' if the delete mode is active.
     */
    Observable<Boolean> observeDelMode();
    void setDelMode(boolean delMode);
    Observable<Set<String>> getSelectedItemsKeys();
    void toggleDeletingSelection(ShowcaseItem item);
    /**
     * Delete from the Showcase all items selected by user for the deleting.
     */
    Completable deleteSelectedShowcaseItems();

    Completable createItem(String name);
    /**
     * Find item by name in all categories.
     * Case sensitive.
     *
     * @param name displayed name of the item
     * @return item domain model or null
     */
    Maybe<Item> getItemByName(String name);

    void updateDisplayedNames();


}
