package alektas.pocketbasket.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import alektas.pocketbasket.db.entity.Item;

@Dao
public interface ShowcaseDao {
    @Query("SELECT * FROM SHOWCASE_ITEMS")
    LiveData<List<Item>> getAll();

    @Query("SELECT * FROM SHOWCASE_ITEMS WHERE 'key' = :name")
    LiveData<Item > getItem(String name);

    @Insert
    void insert(Item item);

    @Insert
    void insertAll(List<Item> items);
}
