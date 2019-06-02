package alektas.pocketbasket.guide.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
    private GuideViewListener mListener;
    private boolean isDisposable = false;

    public static class Builder {
        private final String key;
        private List<View> views;
        private Animator animation;
        private boolean isDisposable;


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
         * Targets for the animation should be applied outside the builder or by the
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
         * Set guide case animation.
         * Targets for the animation should be applied outside the builder or by the
         * {@link Builder#setAnimation(Animator, View...)} method.
         * Disposable means that an animation played once and then a guide case finished.
         * Warning! On disposable cases finish
         * {@link alektas.pocketbasket.guide.domain.Guide#onUserEvent(String)} should be invoked
         * to continue a guide process.
         *
         * @param anim animation which would be started with the guide case.
         * @param isDisposable if true this case would be shown once
         * @return guide case builder
         */
        public Builder setAnimation(Animator anim, boolean isDisposable) {
            animation = anim;
            this.isDisposable = isDisposable;
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

        /**
         * Set guide case animation and apply it to target views.
         * Warning! Targets are not added to the list of guide case views.
         * To add views use {@link Builder#addViews(View...)}
         * Disposable means that an animation played once and then a guide case finished.
         * Warning! On disposable cases finish
         * {@link alektas.pocketbasket.guide.domain.Guide#onUserEvent(String)} should be invoked
         * to continue a guide process.
         *
         * @param anim animation which would be started with the guide case.
         * @param isDisposable if true this case would be shown once
         * @param targets views on which the animation would be applied.
         * @return guide case builder
         */
        public Builder setAnimation(Animator anim, boolean isDisposable, View... targets) {
            animation = anim;
            this.isDisposable = isDisposable;
            for (View view : targets) {
                animation.setTarget(view);
            }
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
        isDisposable = builder.isDisposable;

        if (mAnim != null) {
            mAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    setVisibility(View.VISIBLE);
                    if (mListener != null) mListener.onCaseStart(mKey);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    setVisibility(View.INVISIBLE);
                    if (mListener != null) mListener.onCaseFinish(mKey);
                }
            });
        }
    }

    public void setCaseViewListener(GuideViewListener listener) {
        mListener = listener;
    }

    public void show() {
        if (mAnim != null) {
            mAnim.start();
        } else {
            setVisibility(View.VISIBLE);
            if (mListener != null) mListener.onCaseStart(mKey);
        }

    }

    public void hide() {
        if (mAnim != null) {
            mAnim.end();
        } else {
            setVisibility(View.INVISIBLE);
            if (mListener != null) mListener.onCaseFinish(mKey);
        }
    }

    private void setVisibility(int visibility) {
        for (View view : mViews) {
            view.setVisibility(visibility);
        }
    }

    public boolean isDisposable() {
        return isDisposable;
    }

    public String getKey() {
        return mKey;
    }

    @NonNull
    @Override
    public String toString() {
        return "[ GuideCaseView: key = " + mKey
                + ", animated = " + (mAnim != null)
                + ", disposable = " + isDisposable + " ]";
    }
}





