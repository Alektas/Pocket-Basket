package alektas.pocketbasket.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.Collection;
import java.util.List;

import alektas.pocketbasket.data.db.entities.Item;
import alektas.pocketbasket.data.db.entities.ShowcaseItem;
import io.reactivex.Observable;

@Dao
public abstract class ShowcaseDao {

    @Query("SELECT _key, displayed_name, name_res, img_res, tag_res, deleted, " +
            "(CASE WHEN EXISTS " +
            "(SELECT 1 FROM basket_meta WHERE items._key = basket_meta.item_key) " +
            "THEN 1 ELSE 0 END) AS in_basket " +
            "FROM items WHERE deleted = 0 AND tag_res = :tag " +
            "ORDER BY displayed_name")
    public abstract Observable<List<ShowcaseItem>> getShowcaseItems(String tag);

    @Query("SELECT _key, displayed_name, name_res, img_res, tag_res, deleted, " +
            "(CASE WHEN EXISTS " +
            "(SELECT 1 FROM basket_meta WHERE items._key = basket_meta.item_key) " +
            "THEN 1 ELSE 0 END) AS in_basket " +
            "FROM items WHERE deleted = 0 " +
            "ORDER BY CASE " +
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
            "END, displayed_name")
    public abstract Observable<List<ShowcaseItem>> getShowcaseItems();


    /* Reset showcase queries */

    @Transaction
    public void resetShowcase() {
        deleteUserItems();
        restoreShowcase();
    }

    @Query("DELETE FROM items WHERE name_res IS NULL")
    protected abstract void deleteUserItems();

    @Query("UPDATE items SET deleted = 0")
    public abstract void restoreShowcase();

    @Transaction
    public void deleteItems(Collection<ShowcaseItem> items) {
        for (Item item : items) {
            if (item.getNameRes() == null) {
                delete(item);
                continue;
            }
            item.setDeleted(1);
            update(item);
        }
    }


    /* Default Insert, Update, Delete methods */

    @Update(onConflict = OnConflictStrategy.REPLACE)
    protected abstract void update(Item item);

    @Delete
    protected abstract void delete(Item item);

}
