package alektas.pocketbasket.async;

import android.os.AsyncTask;

import java.util.List;

import alektas.pocketbasket.db.dao.ItemsDao;
import alektas.pocketbasket.db.entity.Item;

public class searchAsync extends AsyncTask<String, Void, List<Item>> {
    private ItemsDao mDao;

    public searchAsync(ItemsDao dao) { mDao = dao; }

    @Override
    protected List<Item> doInBackground(String... query) {
        return mDao.search(query[0]+"%");
    }
}
