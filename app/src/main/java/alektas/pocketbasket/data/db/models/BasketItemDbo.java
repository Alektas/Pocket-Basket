package alektas.pocketbasket.data.db.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;

public class BasketItemDbo extends ItemDbo {
    @ColumnInfo(name = "checked")
    private boolean isChecked;

    public BasketItemDbo(@NonNull String key, @NonNull String name, @Nullable String imgRef,
                         @NonNull String categoryKey, boolean isDeleted, boolean isCustom, boolean isChecked) {
        super(key, name, imgRef, categoryKey, isDeleted, isCustom);
        this.isChecked = isChecked;
    }

    public boolean isChecked() {
        return isChecked;
    }

}
