package alektas.pocketbasket.data.db.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;

public class ItemDbo {
    @NonNull
    @ColumnInfo(name = "_key")
    private String key;

    @NonNull
    private String name;

    @Nullable
    @ColumnInfo(name = "img")
    private String imgRef;

    @NonNull
    @ColumnInfo(name = "category_key")
    private String categoryKey;

    @ColumnInfo(name = "hidden")
    private boolean isDeleted;

    @ColumnInfo(name = "custom")
    private boolean isCustom;

    public ItemDbo(@NonNull String key, @NonNull String name, @Nullable String imgRef,
                   @NonNull String categoryKey, boolean isDeleted, boolean isCustom) {
        this.key = key;
        this.name = name;
        this.imgRef = imgRef;
        this.categoryKey = categoryKey;
        this.isDeleted = isDeleted;
        this.isCustom = isCustom;
    }

    @NonNull
    public String getKey() {
        return key;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @Nullable
    public String getImgRef() {
        return imgRef;
    }

    @NonNull
    public String getCategoryKey() {
        return categoryKey;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isCustom() {
        return isCustom;
    }

}
