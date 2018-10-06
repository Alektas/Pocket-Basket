package alektas.pocketbasket.db;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.NonNull;

import java.util.List;

import alektas.pocketbasket.db.entity.Item;
import alektas.pocketbasket.db.dao.ItemsDao;
import alektas.pocketbasket.model.ItemGenerator;

@Database(entities = {Item.class}, version = 4)
public abstract class AppDatabase extends RoomDatabase {
    private static final String TAG = "AppDatabase";
    private static volatile AppDatabase INSTANCE;
    private static final String DATABASE_NAME = "pocketbasket_db";

    public abstract ItemsDao getDao();

    public static AppDatabase getInstance(final Context context) {
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
                                    new insertAllAsync(getInstance(context).getDao())
                                            .execute(ItemGenerator.getAll());
                                }
                            })
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static class insertAllAsync extends AsyncTask<List<Item>, Void, Void> {
        private ItemsDao mDao;

        insertAllAsync(ItemsDao dao) { mDao = dao; }

        @SafeVarargs
        @Override
        protected final Void doInBackground(List<Item>... items) {
            mDao.insertAll(items[0]);
            return null;
        }
    }
}
