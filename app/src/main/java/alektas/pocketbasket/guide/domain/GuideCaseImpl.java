package alektas.pocketbasket.guide.domain;

import androidx.annotation.NonNull;

public class GuideCaseImpl implements GuideCase {
    private String mKey;

    public GuideCaseImpl(String key) {
        mKey = key;
    }

    @Override
    public String getKey() {
        return mKey;
    }

    @NonNull
    @Override
    public String toString() {
        return "[ GuideCase: key = " + mKey + " ]";
    }
}
