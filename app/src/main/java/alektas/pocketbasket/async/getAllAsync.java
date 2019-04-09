package alektas.pocketbasket.async;

import android.os.AsyncTask;

import java.util.List;

import alektas.pocketbasket.R;
import alektas.pocketbasket.Utils;
import alektas.pocketbasket.db.dao.ItemsDao;
import alektas.pocketbasket.db.entities.Item;

public class getAllAsync extends AsyncTask<String, Void, List<Item>> {
    private ItemsDao mDao;

    public getAllAsync(ItemsDao dao) { mDao = dao; }

    @Override
    protected List<Item> doInBackground(String... tags) {
        if (tags.length == 0
                || tags[0] == null
                || tags[0].equals(Utils.getResIdName(R.string.all))) return mDao.getItems();
        else return mDao.getByTag(tags[0]);
    }
}