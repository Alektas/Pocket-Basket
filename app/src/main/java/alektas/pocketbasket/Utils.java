package alektas.pocketbasket;

import android.content.res.Resources;
import android.util.Log;

public class Utils {
    private static Resources mResources = App.getComponent().context().getResources();

    public static String getString(int stringResId) {
        return mResources.getString(stringResId);
    }

    public static long measureProcessTime(Measurable process) {
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
