package alektas.pocketbasket.async;

import android.os.AsyncTask;

import java.util.List;

import alektas.pocketbasket.R;
import alektas.pocketbasket.Utils;
import alektas.pocketbasket.db.dao.ItemsDao;
import alektas.pocketbasket.db.entity.Item;

public class getBasketItemsAsync extends AsyncTask<Void, Void, List<Item>> {
    private ItemsDao mDao;

    public getBasketItemsAsync(ItemsDao dao) { mDao = dao; }

    @Override
    protected List<Item> doInBackground(Void... voids) {
        return mDao.getBasketItems();
    }
}