package alektas.pocketbasket.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import alektas.pocketbasket.model.PocketBasketContract.*;

public class ShowcaseDB implements Repository {
    private SQLiteOpenHelper mDbHelper;

    ShowcaseDB(Context context, SQLiteOpenHelper dbHelper) {
        mDbHelper = dbHelper;
    }

    @Override
    public void addData(Data data) {
        String name = data.getKey();
        int checked = 0;
        if (data.isChecked()) checked = 1;

        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ShowcaseEntry.COLUMN_NAME, name);
        values.put(ShowcaseEntry.COLUMN_CHECKED, checked);

        // Insert the new row
        db.insert(ShowcaseEntry.TABLE_NAME, null, values);
    }

    public void updateData(String key, boolean checked) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(ShowcaseEntry.COLUMN_CHECKED, checked);

        // Which row to update, based on the title
        String selection = ShowcaseEntry.COLUMN_NAME + " LIKE ?";
        String[] selectionArgs = { key };

        db.update(ShowcaseEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    @Override
    public void deleteData(String key) {
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Define 'where' part of query.
        String selection = ShowcaseEntry.COLUMN_NAME + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { key };

        // Issue SQL statement.
        db.delete(ShowcaseEntry.TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public void clear() {

    }

    @Override
    public Data getData(String key) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ShowcaseEntry._ID,
                ShowcaseEntry.COLUMN_NAME,
                ShowcaseEntry.COLUMN_CHECKED
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = ShowcaseEntry.COLUMN_NAME + " = ?";
        String[] selectionArgs = { key };

        Cursor cursor = db.query(
                ShowcaseEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,           // don't group the rows
                null,            // don't filter by row groups
                null            // don't sort
        );

        Data item = null;
        if (cursor.moveToFirst()) {
            item = getItem(cursor);
        }
        cursor.close();

        return item;
    }

    @Override
    public List<Data> getAll() {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ShowcaseEntry._ID,
                ShowcaseEntry.COLUMN_NAME,
                ShowcaseEntry.COLUMN_CHECKED
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = ShowcaseEntry.COLUMN_NAME + " = ?";
        String[] selectionArgs = { null };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                ShowcaseEntry.COLUMN_NAME + " DESC";

        Cursor cursor = db.query(
                ShowcaseEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,           // don't group the rows
                null,            // don't filter by row groups
                sortOrder               // The sort order
        );

        List<Data> dataList = new ArrayList<>();
        while (cursor.moveToNext()) {
            Data item = getItem(cursor);
            dataList.add(item);
        }
        cursor.close();

        return dataList;
    }

    private Data getItem(Cursor cursor) {
        String name =
                cursor.getString(cursor.getColumnIndexOrThrow(ShowcaseEntry.COLUMN_NAME));
        long checkedL = cursor.getLong(cursor.getColumnIndexOrThrow(ShowcaseEntry.COLUMN_NAME));
        boolean checked = false;
        if (checkedL != 0) checked = true;
        return new Item(name, checked, null);
    }
}
