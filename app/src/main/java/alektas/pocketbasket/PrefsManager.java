package alektas.pocketbasket;

import android.content.SharedPreferences;

import java.util.Map;


public class PrefsManager implements IPrefsManager {
    private SharedPreferences mPreferences;

    PrefsManager(SharedPreferences preferences) {
        mPreferences = preferences;
    }

    @Override
    public void remove(String key) {
        mPreferences.edit().remove(key).apply();
    }

    @Override
    public void clear() {
        mPreferences.edit().clear().apply();
    }

    @Override
    public void add(String key, Boolean checked) {
        mPreferences.edit().putBoolean(key, checked).apply();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Boolean> getAll() {
        return (Map<String, Boolean>)mPreferences.getAll();
    }
}