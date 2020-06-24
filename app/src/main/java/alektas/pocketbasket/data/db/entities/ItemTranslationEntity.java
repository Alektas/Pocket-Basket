package alektas.pocketbasket.data.db.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "item_translations",
        primaryKeys = { "item_key", "lang_code"},
        indices = {
                @Index(value = {"lang_code", "item_key"}, unique = true),
                @Index("item_key"),
                @Index("lang_code")
        },
        foreignKeys = {
                @ForeignKey(
                        entity = ItemEntity.class,
                        parentColumns = {"_key"},
                        childColumns = {"item_key"},
                        onDelete = CASCADE,
                        onUpdate = CASCADE
                ),
                @ForeignKey(
                        entity = LanguageEntity.class,
                        parentColumns = {"code"},
                        childColumns = {"lang_code"},
                        onDelete = CASCADE,
                        onUpdate = CASCADE
                ),
        }
)
public class ItemTranslationEntity {
    @NonNull
    @ColumnInfo(name = "lang_code")
    private String langCode;

    @NonNull
    @ColumnInfo(name = "item_key")
    private String itemKey;

    @NonNull
    private String name;

    public ItemTranslationEntity(@NonNull String itemKey, @NonNull String langCode, @NonNull String name) {
        this.itemKey = itemKey;
        this.langCode = langCode;
        this.name = name;
    }

    @NonNull
    public String getItemKey() {
        return itemKey;
    }

    public String getLangCode() {
        return langCode;
    }

    @NonNull
    public String getName() {
        return name;
    }

}
