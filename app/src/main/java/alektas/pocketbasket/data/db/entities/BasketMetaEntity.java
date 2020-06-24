package alektas.pocketbasket.data.db.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "basket_meta",
        indices = { @Index(value = "item_key", unique = true) },
        foreignKeys = @ForeignKey(
                entity = ItemEntity.class,
                parentColumns = "_key",
                childColumns = "item_key",
                onDelete = CASCADE,
                onUpdate = CASCADE))
public class BasketMetaEntity {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "item_key")
    private String itemKey;

    @ColumnInfo(defaultValue = "0", index = true)
    private int position;

    @ColumnInfo(name = "checked", defaultValue = "0", index = true)
    private boolean isChecked;

    public BasketMetaEntity(@NonNull String itemKey, int position, boolean checked) {
        this.itemKey = itemKey;
        this.position = position;
        this.isChecked = checked;
    }

    @NonNull
    public String getItemKey() {
        return itemKey;
    }

    public int getPosition() {
        return position;
    }

    public boolean isChecked() {
        return isChecked;
    }

}
