package alektas.pocketbasket.di;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.room.Room;

import javax.inject.Named;
import javax.inject.Singleton;

import alektas.pocketbasket.R;
import alektas.pocketbasket.data.db.AppDatabase;
import alektas.pocketbasket.data.db.dao.BasketDao;
import alektas.pocketbasket.data.db.dao.ShowcaseDao;
import dagger.Module;
import dagger.Provides;

@Module
public class StorageModule {
    public static final String APP_PREFERENCES_NAME = "AppPreferences";
    public static final String GUIDE_PREFERENCES_NAME = "GuidePreferences";
    private static final String DATABASE_NAME = "pocketbasket_db";

    @Provides
    @Singleton
    @Named(APP_PREFERENCES_NAME)
    SharedPreferences providesAppPreferences(Context context) {
        return context.getSharedPreferences(
                context.getString(R.string.PREFERENCES_FILE_KEY),
                Context.MODE_PRIVATE
        );
    }

    @Provides
    @Singleton
    @Named(GUIDE_PREFERENCES_NAME)
    SharedPreferences providesGuidePreferences(Context context) {
        return context.getSharedPreferences(
                context.getString(R.string.GUIDE_PREFERENCES_FILE_KEY),
                Context.MODE_PRIVATE
        );
    }

    @Provides
    @Singleton
    ShowcaseDao providesShowcaseDao(AppDatabase database) {
        return database.getShowcaseDao();
    }

    @Provides
    @Singleton
    BasketDao providesBasketDao(AppDatabase database) {
        return database.getBasketDao();
    }

    @Provides
    @Singleton
    AppDatabase providesAppDatabase(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME)
                .createFromAsset("databases/" + DATABASE_NAME + ".db")
                .fallbackToDestructiveMigration()
                .build();
    }

}
