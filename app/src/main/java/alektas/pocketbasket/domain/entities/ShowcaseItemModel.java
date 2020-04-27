package alektas.pocketbasket.domain.entities;

public interface ShowcaseItemModel extends ItemModel {
    boolean getExistInBasket();
    void setExistInBasket(boolean existInBasket);
    boolean isRemoval();
    void setRemoval(boolean removal);
}
