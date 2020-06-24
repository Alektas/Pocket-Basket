package alektas.pocketbasket.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.Collection;
import java.util.List;

import alektas.pocketbasket.data.db.entities.CategoryEntity;
import alektas.pocketbasket.data.db.models.CategoryDbo;
import alektas.pocketbasket.data.db.models.ItemDbo;
import alektas.pocketbasket.data.db.entities.ItemEntity;
import alektas.pocketbasket.data.db.entities.ItemTranslationEntity;
import alektas.pocketbasket.data.db.models.ShowcaseItemDbo;
import io.reactivex.Maybe;
import io.reactivex.Observable;

@Dao
public abstract class ShowcaseDao {

    @Query("SELECT i._key, it.name, i.img, i.category_key, i.hidden, i.custom, " +
            "(CASE WHEN EXISTS " +
                "(SELECT 1 FROM basket_meta WHERE i._key = basket_meta.item_key) " +
            "THEN 1 ELSE 0 END) AS in_basket " +
            "FROM items i, item_translations it, languages l " +
            "WHERE i.hidden = 0 " +
                "AND i.category_key = :categoryKey " +
                "AND i._key = it.item_key " +
                "AND l.code = it.lang_code " +
                "AND l.code = :language " +
            "ORDER BY it.name")
    public abstract Observable<List<ShowcaseItemDbo>> getShowcaseItems(String categoryKey, String language);

    @Query("SELECT i._key, it.name, i.img, i.category_key, i.hidden, i.custom, " +
            "(CASE WHEN EXISTS " +
                "(SELECT 1 FROM basket_meta WHERE i._key = basket_meta.item_key) " +
            "THEN 1 ELSE 0 END) AS in_basket " +
            "FROM items i, item_translations it, languages l, categories c " +
            "WHERE i.hidden = 0 " +
                "AND i._key = it.item_key " +
                "AND l.code = it.lang_code " +
                "AND l.code = :language " +
            "ORDER BY c.id, it.name")
    public abstract Observable<List<ShowcaseItemDbo>> getShowcaseItems(String language);

    @Query("SELECT i._key, it.name, i.img, i.category_key, i.hidden, i.custom " +
            "FROM items i, item_translations it, languages l " +
            "WHERE i.hidden = 0 " +
            "AND i._key = it.item_key " +
            "AND l.code = it.lang_code " +
            "AND l.code = :language " +
            "AND it.name = :name")
    public abstract Maybe<ItemDbo> getItemByName(String name, String language);

    @Query("SELECT i._key, it.name, i.img, i.category_key, i.hidden, i.custom " +
            "FROM items i, item_translations it, languages l " +
            "WHERE it.name LIKE :query " +
            "AND i._key = it.item_key " +
            "AND l.code = it.lang_code " +
            "AND l.code = :language " +
            "AND hidden = 0")
    public abstract Maybe<List<ItemDbo>> search(String query, String language);

    @Query("SELECT c.id, c._key, ct.name " +
            "FROM categories c, category_translations ct, languages l " +
            "WHERE c._key = ct.category_key " +
            "AND l.code = ct.lang_code " +
            "AND l.code = :language " +
            "ORDER BY c.id")
    public abstract Observable<List<CategoryDbo>> getCategories(String language);

    @Transaction
    public void createItem(String name, String categoryKey, String language) {
        ItemEntity item = new ItemEntity(name, null, categoryKey, false, true);
        insert(item);
        ItemTranslationEntity trans = new ItemTranslationEntity(name, language, name);
        insert(trans);
    }

    @Transaction
    public void deleteItems(Collection<String> keys) {
        for (String key : keys) {
            ItemEntity entity = getItemEntity(key);
            if (entity.isCustom()) {
                delete(entity);
                continue;
            }
            hideItem(key);
        }
    }

    @Query("SELECT * FROM items WHERE _key = :key")
    protected abstract ItemEntity getItemEntity(String key);

    @Query("UPDATE items SET hidden = 1 WHERE _key = :key")
    protected abstract void hideItem(String key);

    @Transaction
    public void resetShowcase() {
        deleteUserItems();
        restoreShowcase();
    }

    @Query("DELETE FROM items WHERE custom = 1")
    protected abstract void deleteUserItems();

    @Query("UPDATE items SET hidden = 0")
    public abstract void restoreShowcase();

    @Update(onConflict = OnConflictStrategy.REPLACE)
    protected abstract void update(ItemEntity item);

    @Delete
    protected abstract void delete(ItemEntity item);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract void insert(ItemEntity item);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract void insert(ItemTranslationEntity translation);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(List<ItemEntity> items);

}
