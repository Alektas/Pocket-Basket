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
            // Create the new table
            database.execSQL("CREATE TABLE `items_new` (" +
                    "`name` TEXT NOT NULL, " +
                    "`name_res` INTEGER NOT NULL, " +
                    "`img_res` INTEGER NOT NULL, " +
                    "`checked` INTEGER NOT NULL, " +
                    "`in_basket` INTEGER NOT NULL, " +
                    "`tag_res` INTEGER NOT NULL, " +
                    "PRIMARY KEY(`name`))");
            database.execSQL("CREATE UNIQUE INDEX `index_items_new_name` ON `items_new` (`name`)");
            database.execSQL("CREATE  INDEX `index_items_new_tag_res` ON `items_new` (`tag_res`)");

            // Copy the data
            database.execSQL("INSERT INTO items_new " +
                    "(name, name_res, img_res, checked, in_basket, tag_res) " +
                    "SELECT * FROM items");
            // Remove the old table
            database.execSQL("DROP TABLE items");
            // Change the table name to the correct one
            database.execSQL("ALTER TABLE items_new RENAME TO items");
        }
    };

}
