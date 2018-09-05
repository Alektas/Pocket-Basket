package alektas.pocketbasket.model;

import java.util.List;

public interface Model {
    void addData(String key);
    void deleteData(String key);
    void changeDataState(String key);
    void clearAll();
    Data getData(String key);
    List<Data> getAllItems();
    List<Data> getBasketItems();
}
