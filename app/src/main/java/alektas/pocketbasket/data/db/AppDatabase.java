package alektas.pocketbasket.data.db;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import alektas.pocketbasket.data.db.dao.ItemsDao;
import alektas.pocketbasket.data.db.entities.BasketMeta;
import alektas.pocketbasket.data.db.entities.Item;

@Database(entities = {Item.class, BasketMeta.class}, version = 10)
public abstract class AppDatabase extends RoomDatabase {
    private static final String TAG = "AppDatabase";
    private static volatile AppDatabase INSTANCE;
    private static final String DATABASE_NAME = "pocketbasket_db";

    public abstract ItemsDao getDao();

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    setDatabaseIfNotExists(context.getApplicationContext(), DATABASE_NAME);
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class, DATABASE_NAME)
                            .addMigrations(MIGRATION_8_9, MIGRATION_9_10)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Check if database not exists and copy prepopulated file from assets
    private static void setDatabaseIfNotExists(Context context, String databaseName) {
        final File dbPath = context.getDatabasePath(databaseName);

        // If the database already exists, return
        if (dbPath.exists()) {
            return;
        }

        loadDatabase(context, databaseName, dbPath);
    }

    private static void deleteDatabase(Context context, String databaseName) {
        final File dbPath = context.getDatabasePath(databaseName);

        // If the database already exists, delete it
        if (dbPath.exists()) {
            dbPath.delete();
        }
    }

    private static void loadDatabase(Context context, String databaseName, File dbPath) {
        // Make sure we have a path to the file
        dbPath.getParentFile().mkdirs();

        // Try to copy database file
        try {
            final InputStream inputStream = context.getAssets().open("databases/" + databaseName);
            final OutputStream output = new FileOutputStream(dbPath);

            byte[] buffer = new byte[8192];
            int length;

            while ((length = inputStream.read(buffer, 0, 8192)) > 0) {
                output.write(buffer, 0, length);
            }

            output.flush();
            output.close();
            inputStream.close();
        }
        catch (IOException e) {
            Log.d(TAG, "Failed to open file", e);
            e.printStackTrace();
        }
    }

    // App version upgrade: 0.8.1 -> 0.8.2
    private static final Migration MIGRATION_9_10 = new Migration(9, 10) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("DROP INDEX IF EXISTS `index_items_name`");
            database.execSQL("DROP INDEX IF EXISTS `index_items_tag_res`");
            database.execSQL("DROP INDEX IF EXISTS `index_basket_meta_item_name`");
            database.execSQL("DROP INDEX IF EXISTS `index_basket_meta_position`");

            database.execSQL("CREATE TABLE `items_new` (`_key` TEXT NOT NULL, `displayed_name` TEXT NOT NULL, `name_res` TEXT, `img_res` TEXT, `tag_res` TEXT NOT NULL, `deleted` INTEGER NOT NULL DEFAULT 0, PRIMARY KEY(`_key`))");
            database.execSQL("CREATE UNIQUE INDEX `index_items__key` ON `items_new` (`_key`)");
            database.execSQL("CREATE UNIQUE INDEX `index_items_displayed_name` ON `items_new` (`displayed_name`)");
            database.execSQL("CREATE INDEX `index_items_tag_res` ON `items_new` (`tag_res`)");
            database.execSQL("CREATE INDEX `index_items_deleted` ON `items_new` (`deleted`)");
            database.execSQL("CREATE TABLE `basket_meta_new` (`_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `item_key` TEXT NOT NULL, `position` INTEGER NOT NULL, `marked` INTEGER NOT NULL, FOREIGN KEY(`item_key`) REFERENCES `items`(`_key`) ON UPDATE CASCADE ON DELETE CASCADE )");
            database.execSQL("CREATE UNIQUE INDEX `index_basket_meta_item_key` ON `basket_meta_new` (`item_key`)");
            database.execSQL("CREATE INDEX `index_basket_meta_position` ON `basket_meta_new` (`position`)");

            database.execSQL("INSERT INTO `items_new` (_key, displayed_name, name_res, tag_res, img_res, deleted) " +
                    "SELECT name_res, name, name_res, tag_res, img_res, 0 FROM `items` WHERE name_res NOT NULL");
            database.execSQL("INSERT INTO `basket_meta_new` (_id, item_key, position, marked) " +
                    "SELECT _id, item_name, position, marked FROM `basket_meta`");

            database.execSQL("DROP TABLE `items`");
            database.execSQL("DROP TABLE `basket_meta`");
            database.execSQL("ALTER TABLE `basket_meta_new` RENAME TO `basket_meta`");
            database.execSQL("ALTER TABLE `items_new` RENAME TO `items`");
        }
    };

    // App version upgrade: 0.7.1 -> 0.8.0
    private static final Migration MIGRATION_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            /* Basket items table change */

            database.execSQL("CREATE TABLE IF NOT EXISTS `basket_meta` (" +
                    "`_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`item_name` TEXT NOT NULL, " +
                    "`position` INTEGER NOT NULL, " +
                    "`marked` INTEGER NOT NULL, " +
                    "FOREIGN KEY(`item_name`) REFERENCES `items`(`name`) " +
                    "ON UPDATE CASCADE ON DELETE CASCADE )");
            database.execSQL("DROP INDEX IF EXISTS index_basket_items_item_name");
            database.execSQL("DROP INDEX IF EXISTS index_basket_items_position");
            database.execSQL("CREATE UNIQUE INDEX `index_basket_items_item_name` ON `basket_meta` (`item_name`)");
            database.execSQL("CREATE  INDEX `index_basket_items_position` ON `basket_meta` (`position`)");

            // Insert data from old tables to new ones
            database.execSQL("INSERT INTO basket_meta SELECT * FROM basket_items");
            // Remove old tables
            database.execSQL("DROP TABLE basket_items");
        }
    };

}
