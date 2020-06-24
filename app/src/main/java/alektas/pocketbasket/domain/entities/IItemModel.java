package alektas.pocketbasket.domain.entities;

public interface IItemModel {
    String getKey();
    void setKey(String key);
    String getName();
    void setName(String name);
    String getNameRes();
    void setNameRes(String nameRes);
    String getImgRef();
    void setImgRef(String imgRef);
    String getTagRes();
    void setTagRes(String tagRes);
}
