package alektas.pocketbasket.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "showcase_items")
public class Item implements Data{
    @PrimaryKey
    @ColumnInfo(name = "key")
    @NonNull
    private String mName;
    @ColumnInfo(name = "name_res")
    private int mNameRes;
    @ColumnInfo(name = "img_res")
    private int mImgRes;
    @ColumnInfo(name = "checked")
    private boolean mChecked = false;
    @Ignore
    private int[] mTagsRes;

    public Item(String name) {
        mName = name;
        mNameRes = 0;
        mImgRes = 0;
        mTagsRes = new int[1];
    }

    public Item(int nameRes, int imgRes, int[] tagsRes) {
        mNameRes = nameRes;
        mName = "" + nameRes;
        mImgRes = imgRes;
        mTagsRes = tagsRes;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setNameRes(int nameRes) {
        mNameRes = nameRes;
    }

    public void setImgRes(int imgRes) {
        mImgRes = imgRes;
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
    }

    public void setTagsRes(int[] tagsRes) {
        mTagsRes = tagsRes;
    }

    @Override
    public int getImgRes() {
        return mImgRes;
    }

    @Override
    public int[] getTagsRes() {
        return mTagsRes;
    }

    @Override
    public String getKey() {
        return mName;
    }

    @Override
    public int getNameRes() {
        return mNameRes;
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void check(boolean checkState) {
        mChecked = checkState;
    }

    @Override
    public String toString() {
        return mName;
    }
}
