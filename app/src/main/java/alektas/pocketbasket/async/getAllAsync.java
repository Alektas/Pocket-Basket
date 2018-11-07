package alektas.pocketbasket.async;

import android.os.AsyncTask;

import java.util.List;

import alektas.pocketbasket.db.dao.ItemsDao;
import alektas.pocketbasket.db.entity.Item;

public class getAllAsync extends AsyncTask<Integer, Void, List<Item>> {
    private ItemsDao mDao;

    public getAllAsync(ItemsDao dao) { mDao = dao; }

    @Override
    protected List<Item> doInBackground(Integer... tags) {
        if (tags.length == 0 || tags[0] == 0) return mDao.getItems();
        else return mDao.getByTag(tags[0]);
    }
}