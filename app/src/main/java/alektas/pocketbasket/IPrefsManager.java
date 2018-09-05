package alektas.pocketbasket;

import java.util.Map;

public interface IPrefsManager {
    void remove(String key);
    void clear();
    void add(String key, Boolean checked);
    Map<String, Boolean> getAll();
}
