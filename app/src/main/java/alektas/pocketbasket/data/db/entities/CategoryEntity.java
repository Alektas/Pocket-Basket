package alektas.pocketbasket.data.db.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

@Entity(
        tableName = "categories",
        primaryKeys = { "id", "_key"},
        indices = {
                @Index(value = {"_key", "id"}, unique = true),
                @Index(value = "_key", unique = true)
        }
)
public class CategoryEntity {
    private int id;

    @NonNull
    @ColumnInfo(name = "_key")
    private String key;

    public CategoryEntity(int id, @NonNull String key) {
        this.id = id;
        this.key = key;
    }

    public int getId() {
        return id;
    }

    @NonNull
    public String getKey() {
        return key;
    }

}
