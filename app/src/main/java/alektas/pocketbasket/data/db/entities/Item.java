package alektas.pocketbasket.data.db.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import alektas.pocketbasket.R;
import alektas.pocketbasket.utils.ResourcesUtils;
import alektas.pocketbasket.domain.entities.ItemModel;

@Entity(tableName = "items",
        indices = {
            @Index(value = {"_key"}, unique = true),
            @Index(value = {"displayed_name"}, unique = true),
            @Index("tag_res"),
            @Index("deleted")})
public class Item implements ItemModel {
    @PrimaryKey
    @ColumnInfo(name = "_key")
    @NonNull
    private String mKey;

    @ColumnInfo(name = "displayed_name")
    @NonNull
    private String mName;

    @ColumnInfo(name = "name_res")
    private String mNameRes;

    @ColumnInfo(name = "img_res")
    private String mImgRes;

    @ColumnInfo(name = "tag_res")
    @NonNull
    private String mTagRes;

    private int deleted = 0;

    @Ignore
    public Item(@NonNull String name) {
        mKey = name;
        mName = name;
        mNameRes = null;
        mImgRes = null;
        mTagRes = ResourcesUtils.getResIdName(R.string.other);
    }

    public Item(@NonNull String key, String name, String nameRes, String imgRes, @NonNull String tagRes) {
        mKey = key;
        mName = name;
        mNameRes = nameRes;
        mImgRes = imgRes;
        mTagRes = tagRes;
    }

    @NonNull
    public String getKey() {
        return mKey;
    }

    public void setKey(@NonNull String key) {
        this.mKey = key;
    }

    @NonNull
    @Override
    public String getName() {
        return mName;
    }
    public void setName(@NonNull String name) {
        mName = name;
    }

    public void setNameRes(String nameRes) {
        mNameRes = nameRes;
    }
    @Override
    public String getNameRes() {
        return mNameRes;
    }

    public void setImgRes(String imgRes) {
        mImgRes = imgRes;
    }
    @Override
    public String getImgRes() {
        return mImgRes;
    }

    public void setTagRes(@NonNull String tagRes) {
        mTagRes = tagRes;
    }
    @Override
    public String getTagRes() {
        return mTagRes;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    @NonNull
    public String toString() {
        return "[ Item: key = " + mKey + "; name = " + mName + "; img = " + mImgRes + "; tag = " + mTagRes + " ]";
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != getClass()) return false;
        return ((Item) obj).getKey().equals(this.mKey);
    }
}
