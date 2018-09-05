package alektas.pocketbasket.model;

public interface Data {
    String getKey();
    int getNameRes();
    int getImgRes();
    int[] getTagsRes();
    boolean isChecked();
    void check(boolean checkState);
}
