package alektas.pocketbasket.ui.searching;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import alektas.pocketbasket.data.db.AppDatabase;
import alektas.pocketbasket.data.db.dao.ItemsDao;
import alektas.pocketbasket.data.db.entities.Item;
import alektas.pocketbasket.utils.ResourcesUtils;

public class ItemsProvider extends ContentProvider {
    private static final String TAG = "ItemsProvider";

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {

        String query = uri.getLastPathSegment();
        MatrixCursor cursor = new MatrixCursor(ItemsContract.SEARCH_COLUMNS);
        List<Item> items = new ArrayList<>();
        ItemsDao dao = AppDatabase.getInstance(getContext()).getDao();

        if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(query)) {
            // user hasn't entered anything
            // thus return a default cursor
            return cursor;
        } else {
            // query contains the users search
            // return a cursor with appropriate data
            try {
                items = new searchAsync(dao).execute(query).get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            return fillCursor(cursor, items);
        }
    }

    private Cursor fillCursor(MatrixCursor cursor, List<Item> items) {
        int i = 0;
        for (Item item : items) {
            // Cursor fields assigned in the ItemsContract.
            cursor.addRow(new Object[] {i,                      // ID
                    item.getName(),                             // visible text
                    ResourcesUtils.getImgId(item.getImgRes()),  // image resource ID
                    item.getKey()});                           // data(key = name)
            i++;
        }
        return cursor;
    }

    private static class searchAsync extends AsyncTask<String, Void, List<Item>> {
        private ItemsDao mDao;

        searchAsync(ItemsDao dao) { mDao = dao; }

        @Override
        protected List<Item> doInBackground(String... query) {
            return mDao.search("%" + query[0] + "%");
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
