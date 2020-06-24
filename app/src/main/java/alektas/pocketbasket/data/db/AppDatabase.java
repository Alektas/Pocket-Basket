package alektas.pocketbasket.data.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import alektas.pocketbasket.data.db.dao.BasketDao;
import alektas.pocketbasket.data.db.dao.ShowcaseDao;
import alektas.pocketbasket.data.db.entities.BasketMetaEntity;
import alektas.pocketbasket.data.db.entities.CategoryEntity;
import alektas.pocketbasket.data.db.entities.CategoryTranslationEntity;
import alektas.pocketbasket.data.db.entities.ItemEntity;
import alektas.pocketbasket.data.db.entities.ItemTranslationEntity;
import alektas.pocketbasket.data.db.entities.LanguageEntity;

@Database(
        entities = {
                ItemEntity.class,
                ItemTranslationEntity.class,
                BasketMetaEntity.class,
                CategoryEntity.class,
                CategoryTranslationEntity.class,
                LanguageEntity.class
        },
        version = 13)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ShowcaseDao getShowcaseDao();
    public abstract BasketDao getBasketDao();
}
