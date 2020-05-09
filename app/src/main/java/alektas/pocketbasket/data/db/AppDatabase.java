package alektas.pocketbasket.data.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import alektas.pocketbasket.data.db.dao.BasketDao;
import alektas.pocketbasket.data.db.dao.ShowcaseDao;
import alektas.pocketbasket.data.db.entities.BasketMeta;
import alektas.pocketbasket.data.db.entities.Item;

@Database(entities = {Item.class, BasketMeta.class}, version = 12)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ShowcaseDao getShowcaseDao();
    public abstract BasketDao getBasketDao();
}
