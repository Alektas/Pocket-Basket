package alektas.pocketbasket;

import android.app.Application;

import alektas.pocketbasket.db.AppDatabase;

public class App extends Application {
    private static AppComponent sAppComponent;
    public static AppComponent getComponent() {
        return sAppComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sAppComponent = buildComponent();
    }

    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(this);
    }

    protected AppComponent buildComponent() {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }
}
