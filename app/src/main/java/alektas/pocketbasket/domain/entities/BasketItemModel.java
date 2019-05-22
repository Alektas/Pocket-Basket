package alektas.pocketbasket.domain.entities;

public interface BasketItemModel extends ItemModel {
    boolean isMarked();
    void setMarked(boolean marked);
}
