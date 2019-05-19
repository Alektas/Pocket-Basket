package alektas.pocketbasket.data.db.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "basket_items",
        indices = {
                @Index(value = "item_name", unique = true),
                @Index(value = "position")},
        foreignKeys = @ForeignKey(
                entity = Item.class,
                parentColumns = "name",
                childColumns = "item_name",
                onDelete = CASCADE,
                onUpdate = CASCADE))
public class BasketMeta {

    @PrimaryKey(autoGenerate = true)
    private int _id;

    @ColumnInfo(name = "item_name")
    @NonNull
    private String itemName;

    @NonNull
    private int position;

    @ColumnInfo(name = "checked")
    @NonNull
    private int marked;

    public BasketMeta() { }

    public BasketMeta(String name, int position) {
        itemName = name;
        this.position = position;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
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
        if (marked) this.marked = 1;
        else this.marked = 0;
    }

    public String toString() { return (itemName + ": pos=" + position + " marked=" + marked); }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof BasketMeta) {
            return ((BasketMeta) obj).getItemName().equals(this.itemName);
        }
        return false;
    }
}
