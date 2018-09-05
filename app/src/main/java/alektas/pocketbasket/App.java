package alektas.pocketbasket;

import android.app.Application;

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

    protected AppComponent buildComponent() {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }
}
