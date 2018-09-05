package alektas.pocketbasket.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BasketRepo implements Repository {
    private List<Data> mBasket;

    public BasketRepo() {
        mBasket = new ArrayList<>();
    }

    @Override
    public void addData(Data data) {
        mBasket.add(data);
    }

    @Override
    public void deleteData(String key) {
        for (Iterator<Data> iterator = mBasket.iterator(); iterator.hasNext(); ) {
            Data item = iterator.next();
            if (item.getKey().equals(key)) iterator.remove();
        }
    }

    @Override
    public void clear() {
        mBasket.clear();
    }

    @Override
    public Data getData(String key) {
        for (Data item : mBasket) {
            if (item.getKey().equals(key)) return item;
        }
        return null;
    }

    @Override
    public List<Data> getAll() {
        return mBasket;
    }
}
