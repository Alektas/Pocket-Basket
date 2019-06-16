package alektas.pocketbasket.domain.entities;

public interface ItemModel {
    String getKey();
    void setKey(String key);
    String getName();
    void setName(String name);
    String getNameRes();
    void setNameRes(String nameRes);
    String getImgRes();
    void setImgRes(String imgRes);
    String getTagRes();
    void setTagRes(String tagRes);
}
