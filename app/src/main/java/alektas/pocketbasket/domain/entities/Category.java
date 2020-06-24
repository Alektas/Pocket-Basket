package alektas.pocketbasket.domain.entities;

class Category {
    private String mKey;
    private String mName;

    public Category(String key, String name) {
        mKey = key;
        mName = name;
    }

    public String getKey() {
        return mKey;
    }

    public String getName() {
        return mName;
    }

}
