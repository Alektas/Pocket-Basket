package alektas.pocketbasket.db.dao;

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

    @Query("SELECT * FROM items WHERE in_basket = 1 ORDER BY tag_res, name ASC")
    public abstract LiveData<List<Item>> getBasketData();

    @Query("UPDATE items SET in_basket = 0, checked = 0 WHERE in_basket = 1 AND checked = 1")
    public abstract void clearBasket();

    @Query("DELETE FROM items")
    public abstract void deleteAll();

    @Transaction
    public void fullReset(List<Item> items) {
        deleteAll();
        insertAll(items);
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insert(Item item);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insertAll(List<Item> items);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(Item item);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(List<Item> item);

    @Delete
    public abstract void delete(List<Item> item);
}
