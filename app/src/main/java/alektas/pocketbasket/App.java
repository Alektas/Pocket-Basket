package alektas.pocketbasket;

import android.app.Application;
import android.arch.persistence.room.Room;

import alektas.pocketbasket.model.AppDatabase;

public class App extends Application {
    private static AppComponent sAppComponent;
    private static AppDatabase sDatabase;
    public static AppComponent getComponent() {
        return sAppComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sAppComponent = buildComponent();
    }

    protected AppDatabase getDatabase() {
        if (sDatabase == null) sDatabase = Room.databaseBuilder(
                getApplicationContext(), AppDatabase.class, "pocketbasket_db").build();
        return sDatabase;
    }

    protected AppComponent buildComponent() {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }
}
