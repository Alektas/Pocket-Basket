package alektas.pocketbasket.data.db.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "category_translations",
        primaryKeys = { "category_key", "lang_code"},
        indices = {
                @Index(value = {"category_key", "lang_code"}, unique = true),
                @Index("category_key"),
                @Index("lang_code")
        },
        foreignKeys = {
                @ForeignKey(
                        entity = CategoryEntity.class,
                        parentColumns = {"_key"},
                        childColumns = {"category_key"},
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
public class CategoryTranslationEntity {
    @NonNull
    @ColumnInfo(name = "category_key")
    private String categoryKey;

    @NonNull
    @ColumnInfo(name = "lang_code")
    private String langCode;

    @NonNull
    private String name;

    public CategoryTranslationEntity(@NonNull String categoryKey, @NonNull String langCode, @NonNull String name) {
        this.categoryKey = categoryKey;
        this.langCode = langCode;
        this.name = name;
    }

    @NonNull
    public String getCategoryKey() {
        return categoryKey;
    }

    @NonNull
    public String getLangCode() {
        return langCode;
    }

    @NonNull
    public String getName() {
        return name;
    }

}
