package alektas.pocketbasket.db.entities;

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

    @NonNull
    private int checked;

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

    public int getChecked() {
        return checked;
    }
    public boolean isChecked() {
        return checked != 0;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }
    public void setChecked(boolean checked) {
        if (checked) this.checked = 1;
        else this.checked = 0;
    }

    public String toString() { return (itemName + ": pos=" + position + " checked=" + checked); }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof BasketMeta) {
            return ((BasketMeta) obj).getItemName().equals(this.itemName);
        }
        return false;
    }
}
