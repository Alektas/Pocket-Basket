package alektas.pocketbasket.data.db.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;

import alektas.pocketbasket.domain.entities.BasketItemModel;

public class BasketItem extends Item implements BasketItemModel {
    @ColumnInfo(name = "marked")
    private boolean isMarked;

    public BasketItem(@NonNull String key, String name, String nameRes, String imgRes, @NonNull String tagRes) {
        super(key, name, nameRes, imgRes, tagRes);
    }

    @Override
    public boolean isMarked() {
        return isMarked;
    }

    @Override
    public void setMarked(boolean marked) {
        isMarked = marked;
    }

}
