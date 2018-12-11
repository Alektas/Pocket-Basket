package alektas.pocketbasket.db.dao;

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
    private static final String TAG = "ItemsDao";


    @Query("SELECT * FROM items ORDER BY tag_res, name ASC")
    public abstract List<Item> getItems();

    @Query("SELECT * FROM items WHERE tag_res = :tag ORDER BY name ASC")
    public abstract List<Item> getByTag(String tag);

    @Query("SELECT * FROM items WHERE name LIKE :query")
    public abstract List<Item> search(String query);

    @Query("SELECT * FROM items " +
            "INNER JOIN basket_items on items.name = basket_items.item_name " +
            "GROUP BY basket_items.position")
    public abstract LiveData<List<Item>> getBasketData();

    @Query("SELECT checked FROM basket_items WHERE item_name = :name")
    public abstract int isChecked(String name);


    /* Check item queries */

    @Transaction
    public void check(String name) {
        if (getItemMeta(name).isChecked()) check(name, 0);
        else check(name, 1);
    }

    @Query("UPDATE basket_items SET checked = :state WHERE item_name = :name")
    public abstract void check(String name, int state);

    @Query("SELECT * FROM basket_items WHERE item_name = :name")
    public abstract BasketMeta getItemMeta(String name);


    /* Check all items queries */

    @Transaction
    public void checkAll() {
        if (findUnchecked() == null) {
            checkAll(0);
        }
        else {
            checkAll(1);
        }
    }

    @Query("UPDATE basket_items SET checked = :checked ")
    public abstract void checkAll(int checked);

    @Query("SELECT item_name FROM basket_items WHERE checked = 0 LIMIT 1")
    public abstract String findUnchecked();


    /* Update item positions queries */

    @Transaction
    public void updatePositions(List<Item> items) {
        for (int i = 0; i < items.size(); i++) {
            setPosition(items.get(i).getName(), i + 1);
        }
    }

    @Query("UPDATE basket_items SET position = :position WHERE item_name = :name")
    public abstract void setPosition(String name, int position);


    /* Delete checked items queries */

    @Transaction
    public void deleteChecked() {
        deleteCheckedBasket();
        onDeleteChecked();
    }

    @Query("DELETE FROM basket_items WHERE checked = 1")
    public abstract void deleteCheckedBasket();

    // leads to crush if the positions are not in ascending order
    @Query("UPDATE basket_items SET position = " +
            "(SELECT COUNT(*) FROM basket_items AS t " +
            "WHERE t.position <= basket_items.position)")
    public abstract void onDeleteChecked();


    /* Delete basket item queries */

    @Transaction
    public void deleteBasketItem(String name) {
        int position = getPosition(name);
        deleteFromBasket(name);
        onItemDeleted(position);
    }

    @Query("SELECT position FROM basket_items WHERE item_name = :name")
    public abstract int getPosition(String name);

    @Query("DELETE FROM basket_items WHERE item_name = :name")
    public abstract void deleteFromBasket(String name);

    @Query("UPDATE basket_items SET position = (position - 1) " +
            "WHERE position > :position")
    public abstract void onItemDeleted(int position);


    /* Reset showcase queries */

    @Transaction
    public void fullReset(List<Item> items) {
        deleteAll();
        insertAll(items);
    }

    @Query("DELETE FROM items")
    public abstract void deleteAll();


    /* Add new item to showcase and put item to basket queries */

    @Transaction
    public void addNewItem(Item item) {
        insert(item);
        BasketMeta basketMeta = new BasketMeta();
        basketMeta.setItemName(item.getName());
        basketMeta.setPosition(getMaxPosition() + 1);
        putItemToBasket(basketMeta);
    }

    @Transaction
    public void putItemToBasket(String name) {
        if (getItemMeta(name) != null) return;
        BasketMeta item = new BasketMeta();
        item.setItemName(name);
        item.setPosition(getMaxPosition() + 1);
        putItemToBasket(item);
    }

    @Query("SELECT MAX(position) FROM basket_items")
    public abstract int getMaxPosition();


    /* Default Insert, Update, Delete queries */

    @Insert
    public abstract void putItemToBasket(BasketMeta item);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insert(Item item);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insertAll(List<Item> items);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(Item item);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(List<Item> items);

    @Delete
    public abstract void delete(List<Item> item);
}
