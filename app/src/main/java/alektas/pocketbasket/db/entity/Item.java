package alektas.pocketbasket.db.entity;

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
    private int mNameRes;
    @ColumnInfo(name = "img_res")
    private int mImgRes;
    @ColumnInfo(name = "tag_res")
    private int mTagRes;

    @Ignore
    public Item(@NonNull String name) {
        mName = name;
        mNameRes = 0;
        mImgRes = 0;
        mTagRes = 0;
    }

    @Ignore
    public Item(int nameRes, int imgRes, int tagRes) {
        mNameRes = nameRes;
        mName = "" + nameRes;
        mImgRes = imgRes;
        mTagRes = tagRes;
    }

    public Item(String name, int nameRes, int imgRes, int tagRes) {
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

    public void setNameRes(int nameRes) {
        mNameRes = nameRes;
    }
    public int getNameRes() {
        return mNameRes;
    }

    public void setImgRes(int imgRes) {
        mImgRes = imgRes;
    }
    public int getImgRes() {
        return mImgRes;
    }

    public void setTagRes(int tagRes) {
        mTagRes = tagRes;
    }
    public int getTagRes() {
        return mTagRes;
    }

    public String toString() { return mName; }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj.getClass() != Item.class) return false;
        return ((Item) obj).getName().equals(this.mName);
    }
}
