package alektas.pocketbasket.async;

import android.os.AsyncTask;

import java.util.List;

import alektas.pocketbasket.data.ItemsUpdater;
import alektas.pocketbasket.db.dao.ItemsDao;
import alektas.pocketbasket.db.entities.Item;

public class updateAllAsync extends AsyncTask<List<Item>, Void, Void> {
    private ItemsDao mDao;
    private ItemsUpdater mUpdater;

    public updateAllAsync(ItemsDao dao) {
        mDao = dao;
    }

    public updateAllAsync(ItemsDao dao, ItemsUpdater updater) {
        this(dao);
        mUpdater = updater;
    }

    @SafeVarargs
    @Override
    protected final Void doInBackground(List<Item>... items) {
        mDao.update(items[0]);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (mUpdater != null) {
            mUpdater.updateShowcase();
            mUpdater.updateBasket();
        }
    }
}
