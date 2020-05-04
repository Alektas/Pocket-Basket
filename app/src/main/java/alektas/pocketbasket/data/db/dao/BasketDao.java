package alektas.pocketbasket.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.Collection;
import java.util.List;

import alektas.pocketbasket.data.db.entities.BasketItem;
import alektas.pocketbasket.data.db.entities.BasketMeta;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;

@Dao
public abstract class BasketDao {

    @Query("SELECT _key, displayed_name, name_res, img_res, tag_res, basket_meta.marked, deleted " +
            "FROM items INNER JOIN basket_meta " +
            "ON items._key = basket_meta.item_key " +
            "ORDER BY basket_meta.position")
    public abstract Observable<List<BasketItem>> getItems();

    @Query("SELECT _id, item_key, position, marked FROM basket_meta WHERE item_key = :key")
    public abstract Maybe<BasketMeta> getItemMeta(String key);

    @Query("SELECT item_key FROM basket_meta ORDER BY basket_meta.position")
    protected abstract List<String> getItemKeys();


    /* Mark an item queries */

    @Transaction
    public void toggleItemCheck(String key) {
        BasketMeta meta = getItemMeta(key).blockingGet();
        if (meta == null) return;
        int state = meta.isMarked() ? 0 : 1;
        toggleItemCheck(key, state);

        // Move marked item to the end of the basket list
        if (state != 0) {
            List<String> keys = getItemKeys();
            if (keys.remove(key)) keys.add(key);
            updatePositionsInOrderOf(keys);
        }
    }

    @Query("UPDATE basket_meta SET marked = :state WHERE item_key = :key")
    protected abstract void toggleItemCheck(String key, int state);


    /* Mark all items queries */

    @Transaction
    public void toggleBasketCheck() {
        toggleBasketCheck(findUnchecked() == null ? 0 : 1);
    }

    @Query("UPDATE basket_meta SET marked = :checked ")
    protected abstract void toggleBasketCheck(int checked);

    @Query("SELECT item_key FROM basket_meta WHERE marked = 0 LIMIT 1")
    public abstract String findUnchecked();


    /* Update item positions queries */

    @Transaction
    public void updatePositionsInOrderOf(List<String> keys) {
        // start positions from 1
        int i = 1;
        for (String key : keys) {
            setPosition(key, i);
            i++;
        }
    }

    @Query("UPDATE basket_meta SET position = :position WHERE item_key = :key")
    protected abstract void setPosition(String key, int position);

    @Transaction
    public void updateItemPosition(String key, int position) {
        List<String> keys = getItemKeys();
        int i = keys.indexOf(key);
        if (i < 0 || i == position) return;
        keys.remove(i);
        keys.add(position, key);
        updatePositionsInOrderOf(keys);
    }

    @Transaction
    public void normalizePositions() {
        List<BasketMeta> metas = getMeta();
        int position = 1;
        for (BasketMeta meta : metas) {
            setPosition(meta.getItemKey(), position++);
        }
    }

    @Query("SELECT * FROM basket_meta ORDER BY position")
    protected abstract List<BasketMeta> getMeta();


    /* Delete checked items queries */

    @Transaction
    public boolean removeCheckedItems() {
        if (anyCheckedItem() == null) return false;
        deleteCheckedItems();
        updatePositionsInOrderOf(getItemKeys());
        return true;
    }

    @Query("SELECT _id, item_key, position, marked FROM basket_meta WHERE marked = 1 LIMIT 1")
    protected abstract BasketMeta anyCheckedItem();

    @Query("DELETE FROM basket_meta WHERE marked = 1")
    protected abstract void deleteCheckedItems();


    /* Remove basket item queries */

    @Transaction
    public void removeItems(Collection<String> keys) {
        for (String key : keys) {
            deleteMeta(key);
        }
        normalizePositions();
    }

    @Transaction
    public void removeItem(String key) {
        int position = getPosition(key);
        if (position < 1) return; // no item in basket with this key
        deleteMeta(key);
        onItemDeleted(position);
    }

    @Query("SELECT position FROM basket_meta WHERE item_key = :key")
    protected abstract int getPosition(String key);

    @Query("DELETE FROM basket_meta WHERE item_key = :key")
    protected abstract void deleteMeta(String key);

    @Query("UPDATE basket_meta SET position = (position - 1) " +
            "WHERE position > :position")
    protected abstract void onItemDeleted(int position);

    @Query("DELETE FROM basket_meta")
    public abstract Completable cleanBasket();


    /* Adding items */

    @Transaction
    public void putToBasket(String key) {
        BasketMeta meta = getItemMeta(key).blockingGet();
        if (meta != null) return;
        createMeta(key);
    }

    @Transaction
    protected void createMeta(String key) {
        BasketMeta basketMeta = new BasketMeta(key);
        basketMeta.setPosition(0); // put it to the buffer position
        putBasketMeta(basketMeta);

        List<String> keys = getItemKeys();
        updatePositionsInOrderOf(keys);
    }


    /* Default Insert, Update, Delete methods */

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract void putBasketMeta(BasketMeta meta);

}
