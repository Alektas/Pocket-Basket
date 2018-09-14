package alektas.pocketbasket.model;

import alektas.pocketbasket.IPrefsManager;
import alektas.pocketbasket.db.entity.Item;

import java.util.List;
import java.util.Map;

public class RepoManager implements Model {
    private Repository mBasketRepo;
    private Repository mShowcaseRepo;
    private IPrefsManager mPrefsManager;

    /**
     * @param basketRepo Mutable repository of items. Also: "Basket".
     * @param showcaseRepo Immutable repository of items. Also: "Showcase".
     * @param prefsManager Help to edit preferences, where "Basket" items saved.
     */
    public RepoManager(Repository basketRepo, Repository showcaseRepo, IPrefsManager prefsManager) {
        mPrefsManager = prefsManager;
        mBasketRepo = basketRepo;
        mShowcaseRepo = showcaseRepo;

        // load data to "Basket" from preferences
        for (Map.Entry<String, Boolean> entry :
                mPrefsManager.getAll().entrySet()) {
            String key = entry.getKey();
            boolean checkState = entry.getValue();
            Data data = mShowcaseRepo.getData(key);
            if (data == null) { data = new Item(key); }
            data.check(checkState);
            mBasketRepo.addData(data);
        }
    }

    /* Add item to "Basket" and save it to prefs.
     * Data copied from "Showcase" repo. If it absent, create new.
     * If already exist, don't put item again.
     */
    @Override
    public void addData(String key) {
        if (!isItemExist(key)) {
            Data data = mShowcaseRepo.getData(key);
            if (data == null) data = new Item(key);
            mBasketRepo.addData(data);
            mPrefsManager.add(key, false);
        }
    }

    // Check if there is an item in "Basket"
    private boolean isItemExist(String key) {
        for (Data item :
                mBasketRepo.getAll()) {
            if (item.getKey().equals(key)) {
                return true;
            }
        }
        return false;
    }

    // Change item state in "Basket" and save it
    @Override
    public void changeDataState(String key) {
        Data item = getData(key);
        if (item != null) {
            item.check(!item.isChecked());
            mPrefsManager.add(key, item.isChecked());
        }
    }

    // Delete item from "Basket" and prefs.
    @Override
    public void deleteData(String key) {
        mBasketRepo.deleteData(key);
        mPrefsManager.remove(key);
    }

    // Delete all items from "Basket" and prefs.
    @Override
    public void clearAll() {
        mBasketRepo.clear();
        mPrefsManager.clear();
    }

    // Return item from "Basket" or null if item with this key is absent
    @Override
    public Data getData(String key) {
        for (Data data: mBasketRepo.getAll()) {
            if (data.getKey().equals(key)) return data;
        }
        return null;
    }

    // Return list of items from "Showcase"
    @Override
    public List<Data> getAllItems() {
        return mShowcaseRepo.getAll();
    }

    // Return list of items from "Basket"
    @Override
    public List<Data> getBasketItems() {
        return mBasketRepo.getAll();
    }
}
