package alektas.pocketbasket.data.db.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;

import alektas.pocketbasket.domain.entities.ShowcaseItemModel;

public class ShowcaseItem extends Item implements ShowcaseItemModel {
    @ColumnInfo(name = "in_basket")
    private boolean existInBasket;

    @Ignore
    private boolean isRemoval;

    public ShowcaseItem(@NonNull String key, String name, String nameRes, String imgRes, @NonNull String tagRes) {
        super(key, name, nameRes, imgRes, tagRes);
    }

    public boolean getExistInBasket() {
        return existInBasket;
    }

    public void setExistInBasket(boolean existInBasket) {
        this.existInBasket = existInBasket;
    }

    @Override
    public boolean isRemoval() {
        return isRemoval;
    }

    @Override
    public void setRemoval(boolean removal) {
        isRemoval = removal;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString() + "[ existInBasket = " + existInBasket + "; isRemoval = " + isRemoval + " ]";
    }

    public ShowcaseItem copy() {
        ShowcaseItem item = new ShowcaseItem(getKey(), getName(), getNameRes(), getImgRes(), getTagRes());
        item.setExistInBasket(existInBasket);
        item.setRemoval(isRemoval);
        return item;
    }

}
