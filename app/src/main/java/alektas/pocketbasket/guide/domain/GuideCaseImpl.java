package alektas.pocketbasket.guide.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GuideCaseImpl implements GuideCase {
    private String mKey;
    private GuideCase mLinkedCase;
    private boolean isCompleted;

    public GuideCaseImpl(String key) {
        mKey = key;
    }

    public GuideCaseImpl(String key, boolean completed) {
        mKey = key;
        isCompleted = completed;
    }

    @Override
    public String getKey() {
        return mKey;
    }

    @Override
    public void linkCase(GuideCase guideCase) {
        mLinkedCase = guideCase;
    }

    @Override
    public GuideCase getLinkedCase() {
        return mLinkedCase;
    }

    @Override
    public void complete() {
        isCompleted = true;
    }

    @Override
    public boolean isCompleted() {
        return isCompleted;
    }

    @NonNull
    @Override
    public String toString() {
        return "[ GuideCase: key = " + mKey + ", completed = " + isCompleted + " ]";
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || obj.getClass() != getClass()) return false;
        return ((GuideCaseImpl) obj).getKey().equals(mKey);
    }
}
