package alektas.pocketbasket;

import android.content.res.Resources;

public class Utils {
    private static Resources mResources = App.getComponent().context().getResources();

    public static String getString(int stringResId) {
        return mResources.getString(stringResId);
    }
}
