package alektas.pocketbasket.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ShowcaseDao {
    @Query("SELECT * FROM SHOWCASE_ITEMS")
    List<Item> getAll();

    @Query("SELECT * FROM SHOWCASE_ITEMS WHERE 'key' = :name")
    Item getItem(String name);

    @Insert
    void insert(Item item);
}
