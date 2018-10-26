package alektas.pocketbasket.async;

import android.os.AsyncTask;

import java.util.List;

import alektas.pocketbasket.db.dao.ItemsDao;
import alektas.pocketbasket.db.entity.Item;

public class insertAllAsync extends AsyncTask<List<Item>, Void, Void> {
    private ItemsDao mDao;

    public insertAllAsync(ItemsDao dao) { mDao = dao; }

    @SafeVarargs
    @Override
    protected final Void doInBackground(List<Item>... items) {
        mDao.insertAll(items[0]);
        return null;
    }
}
