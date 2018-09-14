package alektas.pocketbasket.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import alektas.pocketbasket.db.dao.BasketDao;
import alektas.pocketbasket.db.entity.BasketItem;
import alektas.pocketbasket.db.entity.Item;
import alektas.pocketbasket.db.dao.ShowcaseDao;

@Database(entities = {Item.class, BasketItem.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase sInstance;
    private static final String DATABASE_NAME = "pocketbasket_db";

    public abstract ShowcaseDao getShowcaseDao();
    public abstract BasketDao getBasketDao();

    public static AppDatabase getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(
                            context, AppDatabase.class, DATABASE_NAME).build();
                }
            }
        }
        return sInstance;
    }
}
