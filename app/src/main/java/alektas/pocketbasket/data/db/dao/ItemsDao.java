package alektas.pocketbasket.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import alektas.pocketbasket.data.db.entities.BasketMeta;
import alektas.pocketbasket.data.db.entities.Item;

@Dao
public abstract class ItemsDao {
    private static final String TAG = "ItemsDao";

    @Query("SELECT * FROM items ORDER BY CASE " +
            "WHEN tag_res = 'drink' THEN 1 " +
            "WHEN tag_res = 'fruit' THEN 2 " +
            "WHEN tag_res = 'vegetable' THEN 3 " +
            "WHEN tag_res = 'floury' THEN 4 " +
            "WHEN tag_res = 'milky' THEN 5 " +
            "WHEN tag_res = 'groats' THEN 6 " +
            "WHEN tag_res = 'sweets' THEN 7 " +
            "WHEN tag_res = 'meat' THEN 8 " +
            "WHEN tag_res = 'seafood' THEN 9 " +
            "WHEN tag_res = 'semis' THEN 10 " +
            "WHEN tag_res = 'sauce_n_oil' THEN 11 " +
            "WHEN tag_res = 'household' THEN 12 " +
            "WHEN tag_res = 'other' THEN 13 " +
            "ELSE 14 " +
            "END, name")
    public abstract List<Item> getItems();

    @Query("SELECT * FROM items WHERE name = :name")
    public abstract Item getItem(String name);

    @Query("SELECT * FROM items WHERE tag_res = :tag ORDER BY name ASC")
    public abstract List<Item> getByTag(String tag);

    @Query("SELECT * FROM items WHERE name LIKE :query")
    public abstract List<Item> search(String query);

    @Query("SELECT name, name_res, img_res, tag_res FROM items " +
            "INNER JOIN basket_items " +
            "ON items.name = basket_items.item_name " +
            "GROUP BY basket_items.position")
    public abstract LiveData<List<Item>> getBasketData();

    @Query("SELECT name, name_res, img_res, tag_res FROM items " +
            "INNER JOIN basket_items " +
            "ON items.name = basket_items.item_name " +
            "GROUP BY basket_items.position")
    public abstract List<Item> getBasketItems();

    @Query("SELECT name FROM items " +
            "INNER JOIN basket_items " +
            "ON items.name = basket_items.item_name " +
            "GROUP BY basket_items.position")
    protected abstract List<String> getBasketItemNames();

    @Query("SELECT checked FROM basket_items WHERE item_name = :name")
    public abstract int isChecked(String name);


    /* Check item queries */

    @Transaction
    public void check(String name) {
        if (getItemMeta(name).isChecked()) check(name, 0);
        else check(name, 1);
    }

    @Query("UPDATE basket_items SET checked = :state WHERE item_name = :name")
    protected abstract void check(String name, int state);

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
    protected abstract void checkAll(int checked);

    @Query("SELECT item_name FROM basket_items WHERE checked = 0 LIMIT 1")
    public abstract String findUnchecked();


    /* Update item positions queries */

    @Transaction
    public void updatePositions(List<String> names) {
        for (int i = 0; i < names.size(); i++) {
            // start positions from 1
            setPosition(names.get(i), i + 1);
        }
    }

    @Query("UPDATE basket_items SET position = :position WHERE item_name = :name")
    protected abstract void setPosition(String name, int position);


    /* Delete checked items queries */

    @Transaction
    public void deleteChecked() {
        deleteCheckedBasket();
        updatePositions(getBasketItemNames());
    }

    @Query("DELETE FROM basket_items WHERE checked = 1")
    protected abstract void deleteCheckedBasket();


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
    public void addNewItem(String name) {
        Item item = new Item(name);
        insert(item);
        BasketMeta basketMeta = new BasketMeta();
        basketMeta.setItemName(name);
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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
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
