package alektas.pocketbasket.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import alektas.pocketbasket.data.db.entities.Item;
import io.reactivex.Maybe;

@Dao
public abstract class ItemsDao {

    @Query("SELECT _key, displayed_name, name_res, img_res, tag_res, deleted FROM items")
    public abstract Maybe<List<Item>> getAllItems();

    @Query("SELECT _key, displayed_name, name_res, img_res, tag_res, deleted " +
            "FROM items WHERE displayed_name = :name AND deleted = 0")
    public abstract Maybe<Item> getItemByName(String name);

    @Query("SELECT _key, displayed_name, name_res, img_res, tag_res, deleted " +
            "FROM items WHERE displayed_name LIKE :query AND deleted = 0")
    public abstract Maybe<List<Item>> search(String query);


    /* Add new item to showcase and put item to basket queries */
    @Transaction
    public void createItem(String key) {
        Item item = new Item(key);
        insert(item);
    }


    /* Default Insert, Update, Delete methods */

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract void insert(Item item);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(List<Item> items);

}
