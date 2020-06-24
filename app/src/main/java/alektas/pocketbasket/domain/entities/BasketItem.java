package alektas.pocketbasket.domain.entities;

public class BasketItem extends Item {
    private boolean mIsChecked;

    public BasketItem(String key, String name, String imgRef, boolean isChecked) {
        super(key, name, imgRef);
        mIsChecked = isChecked;
    }

    public boolean isChecked() {
        return mIsChecked;
    }

    public void setChecked(boolean checked) {
        mIsChecked = checked;
    }

}
