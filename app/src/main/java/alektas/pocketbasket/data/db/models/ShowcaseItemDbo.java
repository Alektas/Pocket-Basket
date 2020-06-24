package alektas.pocketbasket.data.db.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;

public class ShowcaseItemDbo extends ItemDbo {
    @ColumnInfo(name = "in_basket")
    private boolean isInBasket;

    public ShowcaseItemDbo(@NonNull String key, @NonNull String name, @Nullable String imgRef,
                           @NonNull String categoryKey, boolean isDeleted, boolean isCustom, boolean isInBasket) {
        super(key, name, imgRef, categoryKey, isDeleted, isCustom);
        this.isInBasket = isInBasket;
    }

    public boolean isInBasket() {
        return isInBasket;
    }

}
