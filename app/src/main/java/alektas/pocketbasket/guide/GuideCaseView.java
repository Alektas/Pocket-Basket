package alektas.pocketbasket.guide;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GuideCaseView {
    private static final String TAG = "GuideCaseView";
    private String mKey;
    private List<View> mViews;
    private Animator mAnim;
    private CaseListener mListener;
    private boolean setAnimTarget = false;
    private boolean isAutoNext = false;

    interface CaseListener {
        void onCaseStart(String caseKey);
        void onCaseFinish(String caseKey);
    }

    public static class Builder {
        private final String key;
        private List<View> views;
        private Animator animation;
        private boolean isAutoNext;


        public Builder(String name) {
            this.key = name;
            views = new ArrayList<>();
        }

        public Builder addView(View view) {
            views.add(view);
            return this;
        }

        public Builder addViews(View... views) {
            this.views.addAll(Arrays.asList(views));
            return this;
        }

        /**
         * Set guide case animation.
         * Targets for the animation should be setted inside builder or by the
         * {@link Builder#setAnimation(Animator, View...)} method.
         *
         * @param anim animation which would be started with the guide case.
         * @return guide case builder
         */
        public Builder setAnimation(Animator anim) {
            animation = anim;
            return this;
        }

        /**
         * Set guide case animation and apply it to target views.
         * Warning! Targets are not added to the list of guide case views.
         * To add views use {@link Builder#addViews(View...)}
         *
         * @param anim animation which would be started with the guide case.
         * @param targets views on which the animation would be applied.
         * @return guide case builder
         */
        public Builder setAnimation(Animator anim, View... targets) {
            animation = anim;
            for (View view : targets) {
                animation.setTarget(view);
            }
            return this;
        }

        public Builder setAutoNext(boolean isAutoNext) {
            this.isAutoNext = isAutoNext;
            return this;
        }

        public GuideCaseView build() {
            return new GuideCaseView(this);
        }
    }

    private GuideCaseView(Builder builder) {
        mKey = builder.key;
        mViews = builder.views;
        mAnim = builder.animation;
        isAutoNext = builder.isAutoNext;

        if (mAnim != null) {
            mAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (mListener != null) mListener.onCaseFinish(mKey);
                }
            });
        }
    }

    private GuideCaseView(@NonNull String key, @NonNull View... views) {
        mKey = key;
        mViews = Arrays.asList(views);
    }

    private GuideCaseView(@NonNull String key, AnimatorSet anim, @NonNull View... views) {
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

    private GuideCaseView(@NonNull String key, AnimatorSet anim, boolean setAnimTarget, @NonNull View... views) {
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





