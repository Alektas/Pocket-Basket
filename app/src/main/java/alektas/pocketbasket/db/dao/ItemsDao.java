package alektas.pocketbasket.db.dao;

import android.util.Log;

import alektas.pocketbasket.db.entity.BasketMeta;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import alektas.pocketbasket.db.entity.Item;

@Dao
public abstract class ItemsDao {

    @Query("SELECT * FROM items ORDER BY tag_res, name ASC")
    public abstract List<Item> getItems();

    @Query("SELECT * FROM items WHERE tag_res = :tag ORDER BY name ASC")
    public abstract List<Item> getByTag(int tag);

    @Query("SELECT * FROM items WHERE name LIKE :query")
    public abstract List<Item> search(String query);

    @Query("SELECT * FROM items " +
            "INNER JOIN basket_items on items.name = basket_items.item_name " +
            "GROUP BY basket_items.position")
    public abstract LiveData<List<Item>> getBasketData();

    @Query("SELECT * FROM basket_items")
    public abstract List<BasketMeta> getBasketItems();

    @Query("SELECT * FROM basket_items WHERE item_name = :name")
    public abstract BasketMeta getItemMeta(String name);

    @Query("SELECT checked FROM basket_items WHERE item_name = :name")
    public abstract int isChecked(String name);

    @Query("SELECT MAX(position) FROM basket_items")
    public abstract int getMaxPosition();

    @Query("SELECT position FROM basket_items WHERE item_name = :name")
    public abstract int getPosition(String name);

    @Query("UPDATE basket_items SET checked = :state WHERE item_name = :name")
    public abstract void check(String name, int state);

    @Query("UPDATE basket_items SET checked = :checked ")
    public abstract void checkAll(int checked);

    @Query("UPDATE basket_items SET position = (position + 1) " +
            "WHERE position < :fromPosition " +
            "AND position >= :toPosition")
    // fromPosition > toPosition
    public abstract void onItemUp(int fromPosition, int toPosition);

    @Query("UPDATE basket_items SET position = (position - 1) " +
            "WHERE position > :fromPosition " +
            "AND position <= :toPosition")
    // fromPosition < toPosition
    public abstract void onItemDown(int fromPosition, int toPosition);

    @Query("UPDATE basket_items SET position = :position WHERE item_name = :name")
    public abstract void setPosition(String name, int position);

    @Query("UPDATE basket_items SET position = (position - 1) " +
            "WHERE position > :position")
    public abstract void onItemDeleted(int position);

    // TODO: too slow, replace by setting position in code
    // leads to crush if the positions are not in ascending order
    @Query("UPDATE basket_items SET position = " +
            "(SELECT COUNT(*) FROM basket_items AS t " +
            "WHERE t.position <= basket_items.position)")
    public abstract void onDeleteChecked();

    @Query("DELETE FROM basket_items WHERE item_name = :name")
    public abstract void deleteFromBasket(String name);

    @Query("DELETE FROM basket_items WHERE checked = 1")
    public abstract void deleteCheckedBasket();

    @Query("DELETE FROM items")
    public abstract void deleteAll();

    @Transaction
    public void deleteChecked() {
        deleteCheckedBasket();
        onDeleteChecked();
    }

    @Transaction
    public void deleteBasketItem(String name) {
        int position = getPosition(name);
        deleteFromBasket(name);
        onItemDeleted(position);
    }

    @Transaction
    public void moveItem(String name, int fromPosition, int toPosition) {
        setPosition(name, 0); // free old position for other item
        if (fromPosition > toPosition) onItemUp(fromPosition, toPosition);
        else onItemDown(fromPosition, toPosition);
        setPosition(name, toPosition);
    }

    @Transaction
    public void fullReset(List<Item> items) {
        deleteAll();
        insertAll(items);
    }

    @Transaction
    public void addNewItem(Item item) {
        insert(item);
        BasketMeta basketMeta = new BasketMeta();
        basketMeta.setItemName(item.getName());
        basketMeta.setPosition(getMaxPosition() + 1);
        putItemToBasket(basketMeta);
    }

    @Transaction
    public void check(String name) {
        if (getItemMeta(name).isChecked()) check(name, 0);
        else check(name, 1);
    }

    @Transaction
    public void putItemToBasket(String name) {
        BasketMeta item = new BasketMeta();
        item.setItemName(name);
        item.setPosition(getMaxPosition() + 1);
        putItemToBasket(item);
    }

    @Insert
    public abstract void putItemToBasket(BasketMeta item);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insert(Item item);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insertAll(List<Item> items);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(Item item);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(List<Item> item);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(BasketMeta item);

    @Delete
    public abstract void delete(List<Item> item);
}
