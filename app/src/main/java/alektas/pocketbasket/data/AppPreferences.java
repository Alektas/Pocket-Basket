package alektas.pocketbasket.data;

import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Named;

import alektas.pocketbasket.R;
import alektas.pocketbasket.ui.PrefDefaults;

import static alektas.pocketbasket.di.StorageModule.APP_PREFERENCES_NAME;
import static alektas.pocketbasket.utils.ResourcesUtils.getString;

public class AppPreferences {
    private SharedPreferences mAppPrefs;

    @Inject
    public AppPreferences(@Named(APP_PREFERENCES_NAME) SharedPreferences appPrefs) {
        mAppPrefs = appPrefs;
    }

    public void saveViewMode(boolean isShowcaseMode) {
        mAppPrefs.edit()
                .putBoolean(
                        getString(R.string.IS_SHOWCASE_VIEW_MODE_KEY),
                        isShowcaseMode
                )
                .apply();
    }

    public boolean isShowcaseMode() {
        return mAppPrefs.getBoolean(
                getString(R.string.IS_SHOWCASE_VIEW_MODE_KEY),
                PrefDefaults.IS_SHOWCASE_VIEW_MODE
        );
    }


    public void setHintsShown(boolean isHintsShown) {
        mAppPrefs.edit().putBoolean(getString(R.string.SHOW_HINTS_KEY), isHintsShown).apply();
    }

    public boolean isHintsTurnedOn() {
        return mAppPrefs.getBoolean(getString(R.string.SHOW_HINTS_KEY),PrefDefaults.IS_HINTS_SHOWN);
    }

    public void saveLanguage(String langName) {
        mAppPrefs.edit()
                .putString(getString(R.string.LOCALE_KEY), langName)
                .apply();
    }

    public String getLanguage() {
        return mAppPrefs.getString(getString(R.string.LOCALE_KEY), PrefDefaults.LOCALE_NAME);
    }

    public void saveVersionCode(int version) {
        mAppPrefs.edit()
                .putInt(getString(R.string.VERSION_CODE_KEY), version)
                .apply();
    }

    public int getVersionCode() {
        return mAppPrefs.getInt(getString(R.string.VERSION_CODE_KEY), PrefDefaults.VERSION_CODE);
    }

    public void unsetFirstLaunch() {
        mAppPrefs.edit().putBoolean(getString(R.string.FIRST_START_KEY), false).apply();
    }

    public boolean isFirstLaunch() {
        return mAppPrefs.getBoolean(getString(R.string.FIRST_START_KEY), PrefDefaults.IS_FIRST_LAUNCH);
    }

    public void saveSelectedCategory(int categoryId) {
        mAppPrefs.edit().putInt(getString(R.string.SELECTED_CATEGORY_KEY), categoryId).apply();
    }

    public int getSelectedCategoryId() {
        return mAppPrefs.getInt(getString(R.string.SELECTED_CATEGORY_KEY), PrefDefaults.SELECTED_CATEGORY_ID);
    }

}
