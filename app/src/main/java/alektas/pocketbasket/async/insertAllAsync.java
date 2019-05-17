package alektas.pocketbasket.async;

import android.os.AsyncTask;

import java.util.List;

import alektas.pocketbasket.data.db.dao.ItemsDao;
import alektas.pocketbasket.data.db.entities.Item;
import alektas.pocketbasket.data.ItemsUpdater;

public class insertAllAsync extends AsyncTask<List<Item>, Void, Void> {
    private ItemsDao mDao;
    private ItemsUpdater mUpdater;

    public insertAllAsync(ItemsDao dao) {
        mDao = dao;
    }

    public insertAllAsync(ItemsDao dao, ItemsUpdater updater) {
        this(dao);
        mUpdater = updater;
    }

    @SafeVarargs
    @Override
    protected final Void doInBackground(List<Item>... items) {
        mDao.insertAll(items[0]);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (mUpdater != null) mUpdater.updateShowcase();
    }
}
