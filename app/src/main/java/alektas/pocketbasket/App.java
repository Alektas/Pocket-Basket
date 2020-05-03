package alektas.pocketbasket;

import android.app.Application;

import com.google.firebase.analytics.FirebaseAnalytics;

import alektas.pocketbasket.di.AppComponent;
import alektas.pocketbasket.di.AppModule;
import alektas.pocketbasket.di.DaggerAppComponent;

public class App extends Application {
    private static AppComponent sAppComponent;
    private static FirebaseAnalytics sAnalytics;

    @Override
    public void onCreate() {
        super.onCreate();
        sAppComponent = buildComponent();
        sAnalytics = FirebaseAnalytics.getInstance(this);
    }

    public static FirebaseAnalytics getAnalytics() {
        return sAnalytics;
    }

    public static AppComponent getComponent() {
        return sAppComponent;
    }

    private AppComponent buildComponent() {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }
}
