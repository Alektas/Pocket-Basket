package alektas.pocketbasket;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    Context mContext;
    IPresenter mPresenter;
    IPrefsManager mPrefsManager;
    SharedPreferences mPrefs;

    AppModule(@NonNull Context context) {
        mContext = context;
    }

    @Provides
    SharedPreferences providePrefs() {
        return mContext.getSharedPreferences(
                mContext.getString(R.string.items_prefs), Context.MODE_PRIVATE);
    }

    @Provides
    IPrefsManager providePrefsManager(SharedPreferences prefs) {
        return new PrefsManager(prefs);
    }

    @Provides
    IPresenter providePresenter(IPrefsManager prefsManager) {
        return new Presenter(prefsManager);
    }
}
