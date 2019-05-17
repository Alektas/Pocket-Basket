package alektas.pocketbasket;

import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Locale;

import alektas.pocketbasket.db.entities.Item;

public class Utils {
    private static final String TAG = "Utils";
    private static Resources mResources = App.getComponent().context().getResources();

    public static int getVersionCode() {
        return BuildConfig.VERSION_CODE;
    }

    public static String getVersionName() {
        return BuildConfig.VERSION_NAME;
    }

    public static String getString(int stringResId) {
        return mResources.getString(stringResId);
    }

    public static String getString(@NonNull String stringResName) {
        return getString(getStringId(stringResName));
    }

    public static int getResId(String resName, String type) {
        if (resName == null) return 0;
        return mResources.getIdentifier(resName,
                type,
                App.getComponent().context().getPackageName());
    }

    public static int getImgId(String imgResName) {
        if (imgResName == null) return 0;
        return mResources.getIdentifier(imgResName,
                "drawable",
                App.getComponent().context().getPackageName());
    }

    public static int getStringId(String stringResName) {
        if (stringResName == null) return 0;
        return mResources.getIdentifier(stringResName,
                "string",
                App.getComponent().context().getPackageName());
    }


    public static String getResIdName(int resId) {
        try {
            return mResources.getResourceEntryName(resId);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "getResIdName: resource not found: " + resId, e);
        }
        return null;
    }

    public static String getDisplayName(@NonNull Item item) {
        if (item.getNameRes() == null) return item.getName();
        return getString(item.getNameRes());
    }

    public static Locale getCurrentLocale(){
        return Locale.getDefault();
    }

    public interface Measurable {
        void run();
    }

    public static long measureProcessTime(Measurable process) {
        long start = System.nanoTime();
        process.run();
        long end = System.nanoTime();
        long time = end - start;
        Log.d("ProcessTime", "measureProcessTime: " + time + " ns");
        return time;
    }

    public static long measureProcessTime(Measurable process, String logMessage) {
        Log.d(TAG, "measureProcessTime: " + logMessage);
        long start = System.nanoTime();
        process.run();
        long end = System.nanoTime();
        long time = end - start;
        Log.d("ProcessTime", "measureProcessTime: " + time + " ns");
        return time;
    }

}
