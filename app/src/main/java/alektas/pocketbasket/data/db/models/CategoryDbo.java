package alektas.pocketbasket.data.db.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;

public class CategoryDbo {
    private int id;

    @NonNull
    @ColumnInfo(name = "_key")
    private String key;

    @NonNull
    private String name;

    public CategoryDbo(int id, @NonNull String key, @NonNull String name) {
        this.id = id;
        this.key = key;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    @NonNull
    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

}
