package alektas.pocketbasket.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.Collection;
import java.util.List;

import alektas.pocketbasket.data.db.models.BasketItemDbo;
import alektas.pocketbasket.data.db.entities.BasketMetaEntity;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;

@Dao
public abstract class BasketDao {

    @Query("SELECT i._key, it.name, i.img, i.category_key, i.hidden, i.custom, basket_meta.checked " +
            "FROM items i, item_translations it, languages l " +
            "INNER JOIN basket_meta ON i._key = basket_meta.item_key " +
            "WHERE i._key = it.item_key " +
                "AND l.code = it.lang_code " +
                "AND l.code = :language " +
            "ORDER BY basket_meta.position")
    public abstract Observable<List<BasketItemDbo>> getItems(String language);

    @Query("SELECT item_key, position, checked FROM basket_meta WHERE item_key = :itemKey")
    public abstract Maybe<BasketMetaEntity> getItemMeta(String itemKey);

    @Query("SELECT item_key FROM basket_meta ORDER BY basket_meta.position")
    protected abstract List<String> getItemKeys();


    /* Mark an item queries */

    @Transaction
    public void toggleItemCheck(String key) {
        BasketMetaEntity meta = getItemMeta(key).blockingGet();
        if (meta == null) return;
        toggleItemCheck(key, meta.isChecked());

        // Move marked item to the end of the basket list
        if (meta.isChecked()) {
            List<String> keys = getItemKeys();
            if (keys.remove(key)) keys.add(key);
            updatePositionsInOrderOf(keys);
        }
    }

    @Query("UPDATE basket_meta SET checked = :state WHERE item_key = :itemKey")
    protected abstract void toggleItemCheck(String itemKey, boolean state);


    /* Mark all items queries */

    @Transaction
    public void toggleBasketCheck() {
        toggleBasketCheck(findUnchecked() == null ? 0 : 1);
    }

    @Query("UPDATE basket_meta SET checked = :checked ")
    protected abstract void toggleBasketCheck(int checked);

    @Query("SELECT item_key, position, checked FROM basket_meta WHERE checked = 0 LIMIT 1")
    public abstract BasketMetaEntity findUnchecked();


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

    @Query("UPDATE basket_meta SET position = :position WHERE item_key = :itemKey")
    protected abstract void setPosition(String itemKey, int position);

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
        List<BasketMetaEntity> metas = getMeta();
        int position = 1;
        for (BasketMetaEntity meta : metas) {
            setPosition(meta.getItemKey(), position++);
        }
    }

    @Query("SELECT * FROM basket_meta ORDER BY position")
    protected abstract List<BasketMetaEntity> getMeta();


    /* Delete checked items queries */

    @Transaction
    public boolean removeCheckedItems() {
        if (anyCheckedItem() == null) return false;
        deleteCheckedItems();
        updatePositionsInOrderOf(getItemKeys());
        return true;
    }

    @Query("SELECT item_key, position, checked FROM basket_meta WHERE checked = 1 LIMIT 1")
    protected abstract BasketMetaEntity anyCheckedItem();

    @Query("DELETE FROM basket_meta WHERE checked = 1")
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

    @Query("SELECT position FROM basket_meta WHERE item_key = :itemKey")
    protected abstract int getPosition(String itemKey);

    @Query("DELETE FROM basket_meta WHERE item_key = :itemKey")
    protected abstract void deleteMeta(String itemKey);

    @Query("UPDATE basket_meta SET position = (position - 1) " +
            "WHERE position > :position")
    protected abstract void onItemDeleted(int position);

    @Query("DELETE FROM basket_meta")
    public abstract Completable cleanBasket();


    /* Adding items */

    @Transaction
    public void putToBasket(String key) {
        BasketMetaEntity meta = getItemMeta(key).blockingGet();
        if (meta != null) return;
        createMeta(key);
    }

    @Transaction
    protected void createMeta(String key) {
        // Put new item to the buffer position (0)
        BasketMetaEntity basketMeta = new BasketMetaEntity(key, 0, false);
        putBasketMeta(basketMeta);

        List<String> Keys = getItemKeys();
        updatePositionsInOrderOf(Keys);
    }


    /* Default Insert, Update, Delete methods */

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract void putBasketMeta(BasketMetaEntity meta);

}
