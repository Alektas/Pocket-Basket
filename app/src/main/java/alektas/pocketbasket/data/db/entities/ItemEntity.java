package alektas.pocketbasket.data.db.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import alektas.pocketbasket.data.db.entities.CategoryEntity;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "items",
        foreignKeys = {
                @ForeignKey(
                        entity = CategoryEntity.class,
                        parentColumns = "_key",
                        childColumns = "category_key",
                        onDelete = CASCADE,
                        onUpdate = CASCADE)
        }
)
public class ItemEntity {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "_key", index = true)
    private String key;

    @Nullable
    @ColumnInfo(name = "img")
    private String imgRef;

    @NonNull
    @ColumnInfo(name = "category_key", index = true)
    private String categoryKey;

    @ColumnInfo(name = "hidden", defaultValue = "0", index = true)
    private boolean isDeleted;

    @ColumnInfo(name = "custom", defaultValue = "0", index = true)
    private boolean isCustom;

    public ItemEntity(@NonNull String key, @Nullable String imgRef, @NonNull String categoryKey, boolean isDeleted, boolean isCustom) {
        this.key = key;
        this.imgRef = imgRef;
        this.categoryKey = categoryKey;
        this.isDeleted = isDeleted;
        this.isCustom = isCustom;
    }

    @NonNull
    public String getKey() {
        return key;
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
