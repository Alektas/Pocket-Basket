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
            @Index(value = {"name"}, unique = true),
            @Index("tag_res")})
public class Item implements ItemModel {

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
        mTagRes = ResourcesUtils.getResIdName(R.string.other);
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

    @NonNull
    public String toString() {
        return "[ name = " + mName + "; tag = " + mTagRes + " ]";
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != getClass()) return false;
        return ((Item) obj).getName().equals(this.mName);
    }
}