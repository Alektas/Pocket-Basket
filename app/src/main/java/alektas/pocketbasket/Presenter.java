package alektas.pocketbasket;

import java.util.ArrayList;
import java.util.List;

import alektas.pocketbasket.model.BasketRepo;
import alektas.pocketbasket.model.Data;
import alektas.pocketbasket.model.Model;
import alektas.pocketbasket.model.RepoManager;
import alektas.pocketbasket.model.Repository;
import alektas.pocketbasket.model.ShowcaseRepo;
import alektas.pocketbasket.view.IView;

public class Presenter implements IPresenter {
    private Repository mBasketRepo;
    private Repository mShowcaseRepo;
    private Model mModel;
    private IPrefsManager mPrefsManager;
    private IView mView;
    private int mTag;
    private List<Data> mFilteredItems;
    private boolean isShowcaseMode = true;

    public Presenter(IPrefsManager prefsManager) {
        mPrefsManager = prefsManager;
        mBasketRepo = new BasketRepo();
        mShowcaseRepo = new ShowcaseRepo();
        mModel = new RepoManager(mBasketRepo, mShowcaseRepo, mPrefsManager);
        mFilteredItems = new ArrayList<>(mModel.getAllItems());
    }

    @Override
    public Model getModel() {
        return mModel;
    }

    @Override
    public void attachView(IView view) {
        mView = view;
    }

    @Override
    public void detachView(IView view) {
        mView = null;
    }

    @Override
    public boolean isShowcaseMode() {
        return isShowcaseMode;
    }

    @Override
    public void setShowcaseMode(boolean showcaseMode) {
        isShowcaseMode = showcaseMode;
    }

    @Override
    public void setCategory(int tag) {
        mTag = tag;
        mFilteredItems = getShowcaseItems();
        mView.updateShowcase();
    }

    @Override
    public boolean inBasket(String key) {
        return mModel.getData(key) != null;
    }

    // Change item state in "Basket"
    @Override
    public void checkItem(String key) {
        mModel.changeDataState(key);
        mView.updateBasket();
    }

    @Override
    public void addData(String key) {
        mModel.addData(key);
        mView.updateBasket();
        mView.updateShowcase();
    }

    @Override
    public void deleteData(String key) {
        mModel.deleteData(key);
        mView.updateBasket();
        mView.updateShowcase();
    }

    @Override
    public void deleteAll() {
        mModel.clearAll();
        mView.updateBasket();
        mView.updateShowcase();
    }

    @Override
    public Data getData(String key) {
        return mModel.getData(key);
    }

    private List<Data> filter(List<Data> list) {
        mFilteredItems.clear();
        if (mTag != 0) {
            for (Data data : list) {
                int[] dTags = data.getTagsRes();
                for (int tag : dTags) {
                    if (tag == mTag) {
                        mFilteredItems.add(data);
                    }
                }
            }
        } else mFilteredItems.addAll(list);
        return mFilteredItems;
    }

    @Override
    public List<Data> getShowcaseItems() {
        mFilteredItems = filter(mModel.getAllItems());
        return mFilteredItems;
    }

    @Override
    public List<Data> getBasketItems() {
        return mModel.getBasketItems();
    }
}
