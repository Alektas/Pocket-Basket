package alektas.pocketbasket.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.Collection;
import java.util.List;

import alektas.pocketbasket.data.db.entities.BasketItem;
import alektas.pocketbasket.data.db.entities.BasketMeta;
import alektas.pocketbasket.data.db.entities.Item;
import alektas.pocketbasket.data.db.entities.ShowcaseItem;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;

@Dao
public abstract class ItemsDao {
    private static final String TAG = "ItemsDao";

    @Query("SELECT _key, displayed_name, name_res, img_res, tag_res, deleted, " +
            "(CASE WHEN EXISTS " +
            "(SELECT 1 FROM basket_meta WHERE items._key = basket_meta.item_key) " +
            "THEN 1 ELSE 0 END) AS in_basket " +
            "FROM items WHERE deleted = 0 AND tag_res = :tag " +
            "ORDER BY displayed_name")
    public abstract Observable<List<ShowcaseItem>> getShowcaseItems(String tag);

    @Query("SELECT _key, displayed_name, name_res, img_res, tag_res, deleted, " +
            "(CASE WHEN EXISTS " +
            "(SELECT 1 FROM basket_meta WHERE items._key = basket_meta.item_key) " +
            "THEN 1 ELSE 0 END) AS in_basket " +
            "FROM items WHERE deleted = 0 " +
            "ORDER BY CASE " +
            "WHEN tag_res = 'drink' THEN 1 " +
            "WHEN tag_res = 'fruit' THEN 2 " +
            "WHEN tag_res = 'vegetable' THEN 3 " +
            "WHEN tag_res = 'floury' THEN 4 " +
            "WHEN tag_res = 'milky' THEN 5 " +
            "WHEN tag_res = 'groats' THEN 6 " +
            "WHEN tag_res = 'sweets' THEN 7 " +
            "WHEN tag_res = 'meat' THEN 8 " +
            "WHEN tag_res = 'seafood' THEN 9 " +
            "WHEN tag_res = 'semis' THEN 10 " +
            "WHEN tag_res = 'sauce_n_oil' THEN 11 " +
            "WHEN tag_res = 'household' THEN 12 " +
            "WHEN tag_res = 'other' THEN 13 " +
            "ELSE 14 " +
            "END, displayed_name")
    public abstract Observable<List<ShowcaseItem>> getShowcaseItems();

    @Query("SELECT _key, displayed_name, name_res, img_res, tag_res, deleted FROM items")
    public abstract Maybe<List<Item>> getAllItems();

    @Query("SELECT _key, displayed_name, name_res, img_res, tag_res, deleted " +
            "FROM items WHERE displayed_name = :name AND deleted = 0")
    public abstract Maybe<Item> getItemByName(String name);

    @Query("SELECT _key, displayed_name, name_res, img_res, tag_res, deleted " +
            "FROM items WHERE displayed_name LIKE :query AND deleted = 0")
    public abstract Maybe<List<Item>> search(String query);

    @Query("SELECT _key, displayed_name, name_res, img_res, tag_res, basket_meta.marked, deleted " +
            "FROM items INNER JOIN basket_meta " +
            "ON items._key = basket_meta.item_key " +
            "ORDER BY basket_meta.position")
    public abstract Observable<List<BasketItem>> getBasketItems();

    @Query("SELECT _id, item_key, position, marked FROM basket_meta WHERE item_key = :key")
    public abstract Maybe<BasketMeta> getItemBasketMeta(String key);

    @Query("SELECT _key FROM items " +
            "INNER JOIN basket_meta " +
            "ON items._key = basket_meta.item_key " +
            "ORDER BY basket_meta.position")
    protected abstract List<String> getBasketItemKeys();


    /* Mark an item queries */

    @Transaction
    public void toggleBasketItemCheck(String key) {
        BasketMeta meta = getItemBasketMeta(key).blockingGet();
        if (meta == null) return;
        int state = meta.isMarked() ? 0 : 1;
        toggleBasketItemCheck(key, state);

        // Move marked item to the end of the basket list
        if (state != 0) {
            List<String> keys = getBasketItemKeys();
            if (keys.remove(key)) keys.add(key);
            updateBasketPositions(keys);
        }
    }

    @Query("UPDATE basket_meta SET marked = :state WHERE item_key = :key")
    protected abstract void toggleBasketItemCheck(String key, int state);


    /* Mark all items queries */

    @Transaction
    public void toggleBasketCheck() {
        toggleBasketCheck(findUnmarked() == null ? 0 : 1);
    }

    @Query("UPDATE basket_meta SET marked = :checked ")
    protected abstract void toggleBasketCheck(int checked);

    @Query("SELECT item_key FROM basket_meta WHERE marked = 0 LIMIT 1")
    public abstract String findUnmarked();


    /* Update item positions queries */

    @Transaction
    public void updateBasketPositions(List<String> keys) {
        // start positions from 1
        int i = 1;
        for (String key : keys) {
            setPosition(key, i);
            i++;
        }
    }

    @Transaction
    public void updateBasketItemPosition(String key, int position) {
        List<String> keys = getBasketItemKeys();
        int i = keys.indexOf(key);
        if (i < 0 || i == position) return;
        keys.remove(i);
        keys.add(position, key);
        updateBasketPositions(keys);
    }

    @Query("UPDATE basket_meta SET position = :position WHERE item_key = :key")
    protected abstract void setPosition(String key, int position);


    /* Delete checked items queries */

    @Transaction
    public boolean removeCheckedBasketItems() {
        if (anyMarkedItem() == null) return false;
        deleteCheckedBasket();
        updateBasketPositions(getBasketItemKeys());
        return true;
    }

    @Query("SELECT _id, item_key, position, marked FROM basket_meta WHERE marked = 1 LIMIT 1")
    protected abstract BasketMeta anyMarkedItem();

    @Query("DELETE FROM basket_meta WHERE marked = 1")
    protected abstract void deleteCheckedBasket();


    /* Remove basket item queries */

    @Transaction
    public void removeBasketItem(String key) {
        int position = getPosition(key);
        if (position < 1) return; // no item in basket with this key
        deleteBasketMeta(key);
        onItemDeleted(position);
    }

    @Query("SELECT position FROM basket_meta WHERE item_key = :key")
    protected abstract int getPosition(String key);

    @Query("DELETE FROM basket_meta WHERE item_key = :key")
    protected abstract void deleteBasketMeta(String key);

    @Query("UPDATE basket_meta SET position = (position - 1) " +
            "WHERE position > :position")
    protected abstract void onItemDeleted(int position);

    @Query("DELETE FROM basket_meta")
    public abstract Completable cleanBasket();


    /* Reset showcase queries */

    @Transaction
    public void resetShowcase() {
        deleteUserItems();
        restoreShowcase();
    }

    @Query("DELETE FROM items WHERE name_res IS NULL")
    protected abstract void deleteUserItems();

    @Query("UPDATE items SET deleted = 0")
    public abstract void restoreShowcase();

    @Transaction
    public void deleteItems(Collection<ShowcaseItem> items) {
        for (Item item : items) {
            if (item.getNameRes() == null) {
                delete(item);
                continue;
            }
            item.setDeleted(1);
            update(item);
            removeBasketItem(item.getKey());
        }
    }


    /* Add new item to showcase and put item to basket queries */
    @Transaction
    public void addNewItem(String key) {
        Item item = new Item(key);
        insert(item);
        createBasketMeta(key);
    }

    @Transaction
    public void putToBasket(String key) {
        BasketMeta meta = getItemBasketMeta(key).blockingGet();
        if (meta != null) return;
        createBasketMeta(key);
    }

    @Transaction
    protected void createBasketMeta(String key) {
        BasketMeta basketMeta = new BasketMeta(key);
        basketMeta.setPosition(0);
        putBasketMeta(basketMeta);

        List<String> keys = getBasketItemKeys();
        updateBasketPositions(keys);
    }


    /* Default Insert, Update, Delete methods */

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract void putBasketMeta(BasketMeta meta);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract void insert(Item item);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    protected abstract void update(Item item);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(List<Item> items);

    @Delete
    protected abstract void delete(Item item);

}
