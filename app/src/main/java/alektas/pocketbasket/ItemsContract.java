package alektas.pocketbasket;

import android.app.SearchManager;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ItemsContract {
    public static final String COL_ID = BaseColumns._ID;
    public static final String COL_TEXT = SearchManager.SUGGEST_COLUMN_TEXT_1;
    public static final String COL_ICON = SearchManager.SUGGEST_COLUMN_ICON_1;
    public static final String COL_DATA = SearchManager.SUGGEST_COLUMN_INTENT_DATA;

    public static final String AUTHORITY = BuildConfig.AUTHORITY;
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/items");

    public static final String[] SEARCH_COLUMNS = {
            ItemsContract.COL_ID,
            ItemsContract.COL_TEXT,
            ItemsContract.COL_ICON,
            ItemsContract.COL_DATA
    };
}
