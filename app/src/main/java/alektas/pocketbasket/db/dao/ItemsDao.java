package alektas.pocketbasket.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import alektas.pocketbasket.db.entity.Item;

@Dao
public interface ItemsDao {
    @Query("SELECT * FROM items ORDER BY tag_res, name ASC")
    LiveData<List<Item>> getAll();

    @Query("SELECT * FROM items WHERE tag_res = :tag ORDER BY name ASC")
    LiveData<List<Item>> getByTag(int tag);

    @Query("SELECT * FROM items WHERE in_basket = 1 ORDER BY tag_res, name ASC")
    LiveData<List<Item>> getBasketItems();

    @Query("SELECT * FROM items WHERE name = :name LIMIT 1")
    LiveData<Item> getItem(String name);

    @Query("SELECT * FROM items WHERE name = :name AND in_basket = 1 LIMIT 1")
    LiveData<Item> getBasketItem(String name);

    @Query("UPDATE items SET in_basket = 0 WHERE in_basket = 1")
    void clearBasket();

    @Insert
    void insert(Item item);

    @Insert
    void insertAll(List<Item> items);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(Item item);
}
