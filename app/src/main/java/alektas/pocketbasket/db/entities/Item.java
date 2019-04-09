package alektas.pocketbasket.db.entities;

import alektas.pocketbasket.R;
import alektas.pocketbasket.Utils;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "items",
        indices = {
            @Index(value = {"name"}, unique = true),
            @Index("tag_res")})
public class Item {

    @PrimaryKey
    @ColumnInfo(name = "name")
    @NonNull
    private String mName;
    @ColumnInfo(name = "name_res")
    private String mNameRes;
    @ColumnInfo(name = "img_res")
    private String mImgRes;
    @ColumnInfo(name = "tag_res")
    @NonNull
    private String mTagRes;

    @Ignore
    public Item(@NonNull String name) {
        mName = name;
        mNameRes = null;
        mImgRes = null;
        mTagRes = Utils.getResIdName(R.string.other);
    }

    @Ignore
    public Item(@NonNull String name, @NonNull String tagRes) {
        mName = name;
        mNameRes = null;
        mImgRes = null;
        mTagRes = tagRes;
    }

    @Ignore
    public Item(@NonNull String nameRes, String imgRes, @NonNull String tagRes) {
        mNameRes = nameRes;
        mName = nameRes;
        mImgRes = imgRes;
        mTagRes = tagRes;
    }

    public Item(@NonNull String name, String nameRes, String imgRes, @NonNull String tagRes) {
        mName = name;
        mNameRes = nameRes;
        mImgRes = imgRes;
        mTagRes = tagRes;
    }

    @NonNull
    public String getName() {
        return mName;
    }
    public void setName(@NonNull String name) {
        mName = name;
    }

    public void setNameRes(String nameRes) {
        mNameRes = nameRes;
    }
    public String getNameRes() {
        return mNameRes;
    }

    public void setImgRes(String imgRes) {
        mImgRes = imgRes;
    }
    public String getImgRes() {
        return mImgRes;
    }

    public void setTagRes(String tagRes) {
        mTagRes = tagRes;
    }
    public String getTagRes() {
        return mTagRes;
    }

    public String toString() { return mName; }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj.getClass() != Item.class) return false;
        return ((Item) obj).getName().equals(this.mName);
    }
}
