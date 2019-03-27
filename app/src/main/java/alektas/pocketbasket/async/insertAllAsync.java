package alektas.pocketbasket.async;

import android.os.AsyncTask;

import java.util.List;

import alektas.pocketbasket.db.dao.ItemsDao;
import alektas.pocketbasket.db.entities.Item;
import alektas.pocketbasket.data.Observer;

public class insertAllAsync extends AsyncTask<List<Item>, Void, Void> {
    private ItemsDao mDao;
    private Observer mObserver;

    public insertAllAsync(ItemsDao dao) {
        mDao = dao;
    }

    public insertAllAsync(ItemsDao dao, Observer observer) {
        this(dao);
        mObserver = observer;
    }

    @SafeVarargs
    @Override
    protected final Void doInBackground(List<Item>... items) {
        mDao.insertAll(items[0]);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (mObserver != null) mObserver.update();
    }
}
