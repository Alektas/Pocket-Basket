package alektas.pocketbasket.data.db.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "languages")
public class LanguageEntity {
    @NonNull
    @PrimaryKey
    @ColumnInfo(index = true)
    private String code;

    @ColumnInfo(name = "default", defaultValue = "0")
    private boolean isDefault;

    public LanguageEntity(@NonNull String code, boolean isDefault) {
        this.code = code;
        this.isDefault = isDefault;
    }

    @NonNull
    public String getCode() {
        return code;
    }

    public boolean isDefault() {
        return isDefault;
    }

}
