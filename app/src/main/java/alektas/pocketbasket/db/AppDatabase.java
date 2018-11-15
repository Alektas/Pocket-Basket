package alektas.pocketbasket.db;

import alektas.pocketbasket.async.insertAllAsync;
import alektas.pocketbasket.model.Observer;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import androidx.annotation.NonNull;

import alektas.pocketbasket.db.entity.Item;
import alektas.pocketbasket.db.dao.ItemsDao;
import alektas.pocketbasket.model.ItemGenerator;

@Database(entities = {Item.class}, version = 5)
public abstract class AppDatabase extends RoomDatabase {
    private static final String TAG = "AppDatabase";
    private static volatile AppDatabase INSTANCE;
    private static final String DATABASE_NAME = "pocketbasket_db";

    public abstract ItemsDao getDao();

    public static AppDatabase getInstance(final Context context, Observer observer) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class, DATABASE_NAME)
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    new insertAllAsync(getInstance(context, observer).getDao(),
                                            observer)
                                            .execute(ItemGenerator.getAll());
                                }
                            })
                            .addMigrations(MIGRATION_4_5)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
        }
    };

}
