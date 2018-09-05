package alektas.pocketbasket;

import java.util.List;

import alektas.pocketbasket.model.Data;
import alektas.pocketbasket.view.IView;

public interface IPresenter {
    void attachView(IView view);
    void detachView(IView view);

    void setCategory(int tag);

    boolean isShowcaseMode();
    void setShowcaseMode(boolean showcaseMode);

    boolean inBasket(String key);
    void checkItem(String key);

    void addData(String key);
    void deleteData(String key);
    void deleteAll();
    Data getData(String key);
    List<Data> getAll();
    List<Data> getSelected();
}
