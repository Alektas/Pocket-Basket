package alektas.pocketbasket.db;

import alektas.pocketbasket.R;
import alektas.pocketbasket.Utils;
import alektas.pocketbasket.async.insertAllAsync;
import alektas.pocketbasket.db.entities.BasketMeta;
import alektas.pocketbasket.data.Observer;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;
import android.database.Cursor;

import java.util.List;

import androidx.annotation.NonNull;

import alektas.pocketbasket.db.entities.Item;
import alektas.pocketbasket.db.dao.ItemsDao;
import alektas.pocketbasket.data.ItemGenerator;

@Database(entities = {Item.class, BasketMeta.class}, version = 8)
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
                            .addMigrations(MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `items_new` (" +
                    "`name` TEXT NOT NULL, " +
                    "`name_res` TEXT, " +
                    "`img_res` TEXT, " +
                    "`tag_res` TEXT NOT NULL, " +
                    "PRIMARY KEY(`name`))");
            // Update indexes
            database.execSQL("DROP INDEX IF EXISTS index_items_name");
            database.execSQL("DROP INDEX IF EXISTS index_items_tag_res");
            database.execSQL("CREATE UNIQUE INDEX `index_items_name` ON `items_new` (`name`)");
            database.execSQL("CREATE  INDEX `index_items_tag_res` ON `items_new` (`tag_res`)");

            // Insert item names to the new table
            try (Cursor cursor = database.query("SELECT name FROM items")) {
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex("name"));

                    database.execSQL("INSERT INTO items_new " +
                            "(name, name_res, img_res, tag_res) VALUES ('" +
                            name + "', " +
                            "null, " +
                            "null, '" +
                            Utils.getResIdName(R.string.other) + "')");
                }
            }

            List<Item> newItems =  ItemGenerator.getAll();
            for (Item item : newItems) {
                database.execSQL("UPDATE items_new SET " +
                        "name_res = '" + item.getNameRes() +
                        "', img_res = '" + item.getImgRes() +
                        "', tag_res = '" + item.getTagRes() +
                        "' WHERE name = '" + item.getName() + "'");
            }

            // Remove the old table
            database.execSQL("DROP TABLE items");
            // Change the table name to the correct one
            database.execSQL("ALTER TABLE items_new RENAME TO items");
        }
    };

    private static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `basket_items_new` (" +
                    "`_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`item_name` TEXT NOT NULL, " +
                    "`position` INTEGER NOT NULL, " +
                    "`checked` INTEGER NOT NULL, " +
                    "FOREIGN KEY(`item_name`) REFERENCES `items`(`name`) " +
                    "ON UPDATE CASCADE " +
                    "ON DELETE CASCADE )");

            // Update indexes
            database.execSQL("DROP INDEX IF EXISTS index_basket_items_item_name");
            database.execSQL("DROP INDEX IF EXISTS index_basket_items_position");
            database.execSQL("CREATE UNIQUE INDEX `index_basket_items_item_name` " +
                    "ON `basket_items_new` (`item_name`)");
            database.execSQL("CREATE INDEX `index_basket_items_position` " +
                    "ON `basket_items_new` (`position`)");

            // Copy the data
            database.execSQL("INSERT INTO basket_items_new " +
                    "(item_name, position, checked) " +
                    "SELECT item_name, position, checked FROM basket_items");
            // Remove the old table
            database.execSQL("DROP TABLE basket_items");
            // Change the table name to the correct one
            database.execSQL("ALTER TABLE basket_items_new RENAME TO basket_items");
        }
    };

    private static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `items_new` (" +
                    "`name` TEXT NOT NULL, " +
                    "`name_res` INTEGER NOT NULL, " +
                    "`img_res` INTEGER NOT NULL, " +
                    "`tag_res` INTEGER NOT NULL, " +
                    "PRIMARY KEY(`name`))");
            // Update indexes
            database.execSQL("DROP INDEX IF EXISTS index_items_name");
            database.execSQL("DROP INDEX IF EXISTS index_items_tag_res");
            database.execSQL("CREATE UNIQUE INDEX `index_items_name` ON `items_new` (`name`)");
            database.execSQL("CREATE  INDEX `index_items_tag_res` ON `items_new` (`tag_res`)");

            database.execSQL("CREATE TABLE `basket_items` (" +
                    "`_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`item_name` TEXT NOT NULL, " +
                    "`position` INTEGER NOT NULL, " +
                    "`checked` INTEGER NOT NULL, " +
                    "FOREIGN KEY(`item_name`) REFERENCES `items`(`name`) " +
                    "ON UPDATE CASCADE " +
                    "ON DELETE CASCADE )");
            database.execSQL("CREATE UNIQUE INDEX `index_basket_items_item_name` " +
                    "ON `basket_items` (`item_name`)");
            database.execSQL("CREATE UNIQUE INDEX `index_basket_items_position` " +
                    "ON `basket_items` (`position`)");

            // Copy the data
            database.execSQL("INSERT INTO items_new " +
                    "(name, name_res, img_res, tag_res) " +
                    "SELECT name, name_res, img_res, tag_res FROM items");
            // Remove the old table
            database.execSQL("DROP TABLE items");
            // Change the table name to the correct one
            database.execSQL("ALTER TABLE items_new RENAME TO items");
        }
    };

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
