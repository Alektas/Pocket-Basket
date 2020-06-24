package alektas.pocketbasket.data.db;

public enum LanguageCode {
    ENGLISH("en"), RUSSIAN("ru");

    private final String code;

    LanguageCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
