package alektas.pocketbasket.domain.entities;

public interface BasketItemModel extends ItemModel {
    String getKey();
    void setKey(String key);
    boolean isMarked();
    void setMarked(boolean marked);
}
