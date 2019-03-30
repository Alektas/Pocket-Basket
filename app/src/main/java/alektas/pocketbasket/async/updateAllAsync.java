package alektas.pocketbasket.async;

import android.os.AsyncTask;

import java.util.List;

import alektas.pocketbasket.data.Observer;
import alektas.pocketbasket.db.dao.ItemsDao;
import alektas.pocketbasket.db.entities.Item;

public class updateAllAsync extends AsyncTask<List<Item>, Void, Void> {
    private ItemsDao mDao;
    private Observer mObserver;

    public updateAllAsync(ItemsDao dao) {
        mDao = dao;
    }

    public updateAllAsync(ItemsDao dao, Observer observer) {
        this(dao);
        mObserver = observer;
    }

    @SafeVarargs
    @Override
    protected final Void doInBackground(List<Item>... items) {
        mDao.update(items[0]);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (mObserver != null) mObserver.update();
    }
}
