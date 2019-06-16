package alektas.pocketbasket.data.db.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "basket_meta",
        indices = {
                @Index(value = "item_key", unique = true),
                @Index(value = "position")},
        foreignKeys = @ForeignKey(
                entity = Item.class,
                parentColumns = "_key",
                childColumns = "item_key",
                onDelete = CASCADE,
                onUpdate = CASCADE))
public class BasketMeta {
    @PrimaryKey(autoGenerate = true)
    private int _id;

    @ColumnInfo(name = "item_key")
    @NonNull
    private String itemKey;

    private int position;

    private int marked = 0;

    public BasketMeta(@NonNull String itemKey) {
        this.itemKey = itemKey;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getMarked() {
        return marked;
    }
    public boolean isMarked() {
        return marked != 0;
    }

    public void setMarked(int marked) {
        this.marked = marked;
    }
    public void setMarked(boolean marked) {
        this.marked = marked ? 1 : 0;
    }

    @NonNull
    public String toString() { return ("[basket_meta: name=" + itemKey + ": pos=" + position + " marked=" + marked + "]"); }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != getClass()) return false;
        return ((BasketMeta) obj).getItemKey().equals(this.itemKey);
    }
}
