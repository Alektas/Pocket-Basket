package alektas.pocketbasket.data.db.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Ignore;

import alektas.pocketbasket.domain.entities.ShowcaseItemModel;

public class ShowcaseItem extends Item implements ShowcaseItemModel {
    @Ignore
    private boolean isRemoval;

    public ShowcaseItem(@NonNull String name, String nameRes, String imgRes, @NonNull String tagRes) {
        super(name, nameRes, imgRes, tagRes);
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
