package alektas.pocketbasket.utils;

import android.util.Log;

public class MeasureUtils {
    private static final String TAG = "MeasureUtils";

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
