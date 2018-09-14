package alektas.pocketbasket.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import alektas.pocketbasket.db.entity.BasketItem;

@Dao
public interface BasketDao {
    @Query("SELECT * FROM SHOWCASE_ITEMS")
    LiveData<List<BasketItem>> getAll();

    @Query("SELECT * FROM SHOWCASE_ITEMS WHERE 'key' = :name")
    LiveData<BasketItem> getItem(String name);

    @Insert
    void insert(BasketItem item);

    @Insert
    void insertAll(List<BasketItem> items);

    @Update
    void update(BasketItem item);

    @Delete
    void delete(BasketItem item);
}
