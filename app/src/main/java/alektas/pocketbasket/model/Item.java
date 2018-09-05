package alektas.pocketbasket.model;

public class Item implements Data{
    private String mName;
    private String[] mTags;
    private int mNameRes;
    private int[] mTagsRes;
    private int mImgRes;
    private boolean mChecked = false;

    public Item(String name) {
        mName = name;
        mNameRes = 0;
        mImgRes = 0;
        mTagsRes = null;
    }

    public Item(String name, boolean checked, String[] tags) {
        this(name);
        mChecked = checked;
        mTags = tags;
    }

    public Item(int nameRes, int imgRes, int[] tagsRes) {
        mNameRes = nameRes;
        mName = "" + nameRes;
        mImgRes = imgRes;
        mTagsRes = tagsRes;
    }

    @Override
    public int getImgRes() {
        return mImgRes;
    }

    @Override
    public int[] getTagsRes() {
        return mTagsRes;
    }

    @Override
    public String getKey() {
        return mName;
    }

    @Override
    public int getNameRes() {
        return mNameRes;
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void check(boolean checkState) {
        mChecked = checkState;
    }

    @Override
    public String toString() {
        return mName;
    }
}
