package alektas.pocketbasket.data.db.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;

public class BasketItem extends Item {
    @ColumnInfo(name = "marked")
    private boolean isMarked;

    public BasketItem(@NonNull String key, String name, String nameRes, String imgRes, @NonNull String tagRes) {
        super(key, name, nameRes, imgRes, tagRes);
    }

    public boolean isMarked() {
        return isMarked;
    }

    public void setMarked(boolean marked) {
        isMarked = marked;
    }

}
