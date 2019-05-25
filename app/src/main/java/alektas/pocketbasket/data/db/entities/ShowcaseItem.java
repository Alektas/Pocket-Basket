package alektas.pocketbasket.data.db.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;

import alektas.pocketbasket.domain.entities.ShowcaseItemModel;

public class ShowcaseItem extends Item implements ShowcaseItemModel {
    @ColumnInfo(name = "in_basket")
    private int existInBasket;

    @Ignore
    private boolean isRemoval;

    public ShowcaseItem(@NonNull String name, String nameRes, String imgRes, @NonNull String tagRes) {
        super(name, nameRes, imgRes, tagRes);
    }

    public int getExistInBasket() {
        return existInBasket;
    }
    public boolean isExistInBasket() {
        return existInBasket != 0;
    }

    public void setExistInBasket(int existInBasket) {
        this.existInBasket = existInBasket;
    }
    public void setExistInBasket(boolean existInBasket) {
        this.existInBasket = existInBasket ? 1 : 0;
    }

    @Override
    public boolean isRemoval() {
        return isRemoval;
    }

    @Override
    public void setRemoval(boolean removal) {
        isRemoval = removal;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != getClass()) return false;
        ShowcaseItem item = (ShowcaseItem) obj;
        return item.getName().equals(getName());
    }
}
