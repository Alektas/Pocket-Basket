package alektas.pocketbasket.widget;

public class WidgetBasketItem {
    private String mName;
    private String mIconName;
    private boolean isRemoval;

    public WidgetBasketItem(String name) {
        mName = name;
    }

    public WidgetBasketItem(String name, String iconName) {
        mName = name;
        mIconName = iconName;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getIconName() {
        return mIconName;
    }

    public void setIconName(String iconName) {
        mIconName = iconName;
    }

    public boolean isRemoval() {
        return isRemoval;
    }

    public void setRemoval(boolean removal) {
        isRemoval = removal;
    }
}
