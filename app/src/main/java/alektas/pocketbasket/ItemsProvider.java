package alektas.pocketbasket;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import alektas.pocketbasket.db.AppDatabase;
import alektas.pocketbasket.db.dao.ItemsDao;
import alektas.pocketbasket.db.entity.Item;
import alektas.pocketbasket.async.searchAsync;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
        ItemsDao dao = AppDatabase.getInstance(getContext(), null).getDao();

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
            // ID, name, image resource ID and data(name)
            cursor.addRow(new Object[] {i, item.getName(), item.getImgRes(), item.getName()});
            i++;
        }
        return cursor;
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
