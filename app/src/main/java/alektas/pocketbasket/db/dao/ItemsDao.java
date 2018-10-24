package alektas.pocketbasket.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import alektas.pocketbasket.db.entity.Item;

@Dao
public interface ItemsDao {

    @Query("SELECT * FROM items ORDER BY tag_res, name ASC")
    List<Item> getItems();

    @Query("SELECT * FROM items WHERE tag_res = :tag ORDER BY name ASC")
    List<Item> getByTag(int tag);

    @Query("SELECT * FROM items WHERE in_basket = 1 ORDER BY tag_res, name ASC")
    LiveData<List<Item>> getBasketData();

    @Query("UPDATE items SET in_basket = 0, checked = 0 WHERE in_basket = 1 AND checked = 1")
    void clearBasket();

    @Insert
    void insert(Item item);

    @Insert
    void insertAll(List<Item> items);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(Item item);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(List<Item> item);

    @Delete
    void delete(List<Item> item);
}
