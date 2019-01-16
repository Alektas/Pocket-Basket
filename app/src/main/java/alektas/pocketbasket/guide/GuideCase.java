package alektas.pocketbasket.guide;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.util.Log;
import android.view.View;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;

public class GuideCase {
    private static final String TAG = "GuideCase";
    private String mKey;
    private List<View> mViews;
    private AnimatorSet mAnim;
    private CaseListener mListener;
    private boolean setAnimTarget = false;
    private boolean isAutoNext = false;

    interface CaseListener {
        void onCaseStart(String caseKey);
        void onCaseFinish(String caseKey);
    }

    public GuideCase(@NonNull String key, @NonNull View... views) {
        mKey = key;
        mViews = Arrays.asList(views);
    }

    public GuideCase(@NonNull String key, AnimatorSet anim, @NonNull View... views) {
        this(key, views);
        mAnim = anim;
        mAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mListener != null) mListener.onCaseFinish(key);
            }
        });
    }

    public GuideCase(@NonNull String key, AnimatorSet anim, boolean setAnimTarget, @NonNull View... views) {
        this(key, anim, views);
        this.setAnimTarget = setAnimTarget;
    }

    /* Next case will appear automatically.
     * Works only if it is an animated case. */
    public void setAutoNext(boolean isAutoNext) {
        this.isAutoNext = isAutoNext;
    }

    public boolean isAutoNext() {
        return isAutoNext;
    }

    public String getKey() {
        return mKey;
    }

    public void show() {
        if (mAnim != null) {
            if (setAnimTarget) mAnim.setTarget(mViews.get(0));
            mAnim.start();
        }
        setVisibility(View.VISIBLE);

        mListener.onCaseStart(mKey);
    }

    public void hide() {
        if (mAnim != null) {
            if (!isAutoNext) mAnim.end();
            if (setAnimTarget) mAnim.setTarget(null);
        }
        setVisibility(View.INVISIBLE);
    }

    private void setVisibility(int visibility) {
        for (View view : mViews) {
            view.setVisibility(visibility);
        }
    }

    public void setCaseListener(CaseListener listener) {
        mListener = listener;
    }

    public void removeCaseListener() {
        mListener = null;
    }

    @NonNull
    @Override
    public String toString() {
        return mKey;
    }
}





