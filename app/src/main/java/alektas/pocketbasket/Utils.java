package alektas.pocketbasket;

import android.content.res.Resources;
import android.util.Log;

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

    public static String getString(String stringResName) {
        return getString(getStringId(stringResName));
    }

    public static int getResId(String idName, String type) {
        if (idName == null) return 0;
        return mResources.getIdentifier(idName,
                type,
                App.getComponent().context().getPackageName());
    }

    public static int getImgId(String idName) {
        if (idName == null) return 0;
        return mResources.getIdentifier(idName,
                "drawable",
                App.getComponent().context().getPackageName());
    }

    public static int getStringId(String idName) {
        if (idName == null) return 0;
        return mResources.getIdentifier(idName,
                "string",
                App.getComponent().context().getPackageName());
    }


    public static String getResIdName(int id) {
        try {
            return mResources.getResourceEntryName(id);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "getResIdName: resource not found: " + id, e);
        }
        return null;
    }

    public static String getDisplayName(Item item) {
        if (item.getNameRes() == null) return item.getName();
        return getString(item.getNameRes());
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

    public interface Measurable {
        void run();
    }
}
