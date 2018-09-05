package alektas.pocketbasket.model;

import java.util.List;

public interface Repository {
    void addData(Data data);
    void deleteData(String key);
    void clear();
    Data getData(String key);
    List<Data> getAll();
}
