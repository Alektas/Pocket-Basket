package alektas.pocketbasket.utils;

import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.AnyRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import java.util.Locale;

import alektas.pocketbasket.App;
import alektas.pocketbasket.BuildConfig;

public class ResourcesUtils {
    private static final String TAG = "ResourcesUtils";
    private static Resources mResources = App.getComponent().context().getResources();

    public static int getVersionCode() {
        return BuildConfig.VERSION_CODE;
    }

    public static String getVersionName() {
        return BuildConfig.VERSION_NAME;
    }

    public static String getString(@NonNull String stringResName) {
        return getString(getStringId(stringResName));
    }

    public static String getString(@StringRes int stringResId) {
        return mResources.getString(stringResId);
    }

    public static int getImgId(String imgResName) {
        if (imgResName == null) return 0;
        return getResId(imgResName, "drawable");
    }

    public static int getStringId(String stringResName) {
        if (stringResName == null) return 0;
        return getResId(stringResName, "string");
    }

    public static int getResId(@NonNull String resName, @NonNull String type) {
        return mResources.getIdentifier(
                resName,
                type,
                App.getComponent().context().getPackageName()
        );
    }


    public static String getResIdName(@AnyRes int resId) {
        try {
            return mResources.getResourceEntryName(resId);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "getResIdName: resource not found: " + resId, e);
        }
        return null;
    }

    public static String getCurrentLanguage() {
        return Locale.getDefault().getLanguage();
    }

}
