package alektas.pocketbasket.data.db.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;

import alektas.pocketbasket.domain.entities.BasketItemModel;

public class BasketItem extends Item implements BasketItemModel {
    @ColumnInfo(name = "marked")
    private boolean isMarked;

    public BasketItem(@NonNull String name, String nameRes, String imgRes, @NonNull String tagRes) {
        super(name, nameRes, imgRes, tagRes);
    }

    @Override
    public boolean isMarked() {
        return isMarked;
    }

    @Override
    public void setMarked(boolean marked) {
        isMarked = marked;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != getClass()) return false;
        BasketItem item = (BasketItem) obj;
        return item.getName().equals(getName())
                && item.isMarked() == isMarked;
    }
}
