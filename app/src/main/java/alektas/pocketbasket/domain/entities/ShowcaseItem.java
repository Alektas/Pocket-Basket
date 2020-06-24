package alektas.pocketbasket.domain.entities;

public class ShowcaseItem extends Item {
    private boolean mIsInBasket;
    private boolean mIsSelected;

    public ShowcaseItem(String key, String name, String imgRef, boolean isInBasket, boolean isSelected) {
        super(key, name, imgRef);
        mIsInBasket = isInBasket;
        mIsSelected = isSelected;
    }

    public boolean isInBasket() {
        return mIsInBasket;
    }

    public void setInBasket(boolean inBasket) {
        mIsInBasket = inBasket;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public void setSelected(boolean selected) {
        mIsSelected = selected;
    }

    public ShowcaseItem copy() {
        return new ShowcaseItem(
                getKey(),
                getName(),
                getImgRef(),
                isInBasket(),
                isSelected()
        );
    }

}
