package alektas.pocketbasket.model;

import android.provider.BaseColumns;

public final class PocketBasketContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private PocketBasketContract() {}

    /* Inner class that defines the table contents */
    public static class ShowcaseEntry implements BaseColumns {
        public static final String TABLE_NAME = "showcase";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_CHECKED = "checked";
    }

    public static class BasketEntry implements BaseColumns {
        public static final String TABLE_NAME = "basket";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_CHECKED = "checked";
    }
}
