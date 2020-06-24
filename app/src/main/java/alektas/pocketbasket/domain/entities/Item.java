package alektas.pocketbasket.domain.entities;

import androidx.annotation.NonNull;

public class Item {
    @NonNull
    private String mKey;
    @NonNull
    private String mName;
    private String mImgRef;

    public Item(@NonNull String key, @NonNull String name, String imgRef) {
        mKey = key;
        mName = name;
        mImgRef = imgRef;
    }

    @NonNull
    public String getKey() {
        return mKey;
    }

    public void setKey(@NonNull String key) {
        mKey = key;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    public void setName(@NonNull String name) {
        mName = name;
    }

    public String getImgRef() {
        return mImgRef;
    }

    public void setImgRef(String imgRef) {
        mImgRef = imgRef;
    }

}
