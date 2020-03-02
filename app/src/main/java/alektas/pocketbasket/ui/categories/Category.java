package alektas.pocketbasket.ui.categories;

public class Category {
    private int id;
    private String name;
    private int nameRes;
    private int iconRes;

    public Category(int id, String name, int nameRes, int iconRes) {
        this.id = id;
        this.name = name;
        this.nameRes = nameRes;
        this.iconRes = iconRes;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getNameRes() {
        return nameRes;
    }

    public int getIconRes() {
        return iconRes;
    }
}
