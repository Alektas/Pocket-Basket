package alektas.pocketbasket.ui;

import android.content.res.Configuration;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import androidx.recyclerview.widget.RecyclerView;

import alektas.pocketbasket.R;
import alektas.pocketbasket.ui.utils.SmoothDecelerateInterpolator;

public class ViewModeDelegate {
    private static final String TAG = "ViewModeDelegate";
    private static final float CHANGE_MODE_MIN_VELOCITY = 150;
    /**
     * Affect to interpolator selection for the mode change.
     * The smaller the divider, the easier faster interpolator is selecting.
     */
    private static final float CHANGE_MODE_VELOCITY_DIVIDER = 1000;
    private static final long CHANGE_MODE_TIME = 250;
    private MainActivity mActivity;
    private ActivityViewModel mViewModel;
    private DimensionsProvider mDimens;
    private VelocityTracker mVelocityTracker;
    private ViewGroup mRootLayout;
    private View mCategoriesContainer;
    private View mBasketContainer;
    private View mShowcaseContainer;
    private RecyclerView mShowcase;
    private RecyclerView mBasket;
    private TransitionSet mChangeModeTransition;
    private Transition mChangeBounds;
    private int initX;
    private int initY;
    private int movX;
    private boolean allowChangeMode = true;
    private boolean isChangeModeHandled = false;
    private boolean isLandscapeMode;

    public ViewModeDelegate(MainActivity activity, ActivityViewModel viewModel, DimensionsProvider dimensProvider) {
        mActivity = activity;
        mViewModel = viewModel;
        mDimens = dimensProvider;
        isLandscapeMode = activity.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
        
        initViews();
        initTransitions();

        mViewModel.setOrientationState(isLandscapeMode);
        mViewModel.showcaseModeState().observe(activity, isShowcase -> {
            // Change mode enabled only if it's not a landscape layout
            if (isLandscapeMode) return;
            if (isShowcase) {
                applyShowcaseModeLayout();
            } else {
                applyBasketModeLayout();
            }
        });


        if (isLandscapeMode) {
            applyLandscapeLayout();
        }
    }

    /**
     * Apply sizes of the layout parts in consist of the Showcase Mode:
     * categories and showcase expanded, basket narrowed.
     * Warning! This method don't change global mode state and it should be
     * invoked only when the mode is not changed, but it's necessary to apply appropriate sizes.
     * <p>
     * To actually change mode invoke {@link #setShowcaseMode() setShowcaseMode} instead.
     */
    private void applyShowcaseModeLayout() {
        TransitionManager.beginDelayedTransition(mRootLayout, mChangeModeTransition);
        changeLayoutSize(mDimens.getCategWideWidth(),
                0,
                mDimens.getBasketNarrowWidth());
    }

    /**
     * Apply sizes of the layout parts in consist of the Basket Mode:
     * categories and showcase narrowed, basket expanded.
     * Warning! This method don't change global mode state and it should be
     * invoked only when the mode is not changed, but it's necessary to apply appropriate sizes.
     * <p>
     * To actually change mode invoke {@link #setBasketMode() setBasketMode} instead.
     */
    private void applyBasketModeLayout() {
        TransitionManager.beginDelayedTransition(mRootLayout, mChangeModeTransition);
        changeLayoutSize(mDimens.getCategNarrowWidth(),
                mDimens.getShowcaseNarrowWidth(),
                0);
    }

    /**
     * Set Landscape Mode: categories, showcase and basket expanded
     */
    private void applyLandscapeLayout() {
        changeLayoutSize(mDimens.getCategWideWidth(),
                mDimens.getShowcaseWideWidth(),
                0);
    }

    /**
     * Intercept user touch to handle changing mode.
     */
    public void onTouch(MotionEvent event) {
        // Do not allow a mode change in the landscape orientation
        if (!isLandscapeMode) {
            handleChangeModeByTouch(event);
            // When changing mode is active cancel all other actions to avoid fake clicks
            // Also stop scrolling to avoid crashing
            if (isModeChanging()) {
                mShowcase.stopScroll();
                mBasket.stopScroll();
                event.setAction(MotionEvent.ACTION_CANCEL);
            }
        }
    }

    private boolean isModeChanging() {
        return allowChangeMode && isChangeModeHandled;
    }

    private void handleChangeModeByTouch(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                Log.d(TAG, "handleChangeModeByTouch: ACTION_DOWN");

                initX = (int) (event.getX() + 0.5f);
                initY = (int) (event.getY() + 0.5f);

                /* Disable the change mode from the basket in the basket mode
                 * because direction of the swipe is match to direction of the item delete swipe.
                 * Also disable if it's a touch on the bottom app bar.
                 */
                if (!mViewModel.isShowcaseMode()
                        && initX > (mDimens.getCategNarrowWidth() + mDimens.getShowcaseNarrowWidth())
                        || isInteractionWithSystemBars(initY)) {
                    allowChangeMode = false;
                    isChangeModeHandled = true;
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                Log.d(TAG, "handleChangeModeByTouch: ACTION_MOVE");
                // Do not handle change mode if it didn't allowed
                if (!allowChangeMode && isChangeModeHandled) {
                    return;
                }

                movX = (int) (event.getX() + 0.5f - initX);
                int movY = (int) (event.getY() + 0.5f - initY);

                // Allow or disallow changing mode
                if (!isChangeModeHandled) {
                    if (Math.abs(movY) > Math.abs(movX)) {
                        // Vertical direction is dominate so change mode is not allowed
                        isChangeModeHandled = true;
                        allowChangeMode = false;
                        return;
                    }
                    if (Math.abs(movX) < mDimens.getChangeModeStartDistance()) {
                        isChangeModeHandled = false;
                        return; // gesture length is not enough to start change mode handling
                    }
                    isChangeModeHandled = true;
                    allowChangeMode = true;
                }

                changeLayoutSizeByTouch(movX);
                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                Log.d(TAG, "handleChangeModeByTouch: ACTION_UP/CANCEL");
                finishModeChange();
                break;
            }
        }
    }

    private boolean isInteractionWithSystemBars(int touchDownY) {
        return touchDownY > mDimens.getBottomAppBarY();
    }

    /**
     * Set size of the layout parts in depends of touch moving distance
     *
     * @param movX touch moving distance
     */
    private void changeLayoutSizeByTouch(int movX) {
        int moveHalf = movX / 2;
        int showcaseOffset;
        int categOffset;
        if (mViewModel.isShowcaseMode()) {
            showcaseOffset = mDimens.getShowcaseMoveRange() + moveHalf;
            categOffset = mDimens.getCategMoveRange() + moveHalf;
        } else {
            showcaseOffset = moveHalf - mDimens.getChangeModeStartDistance();
            categOffset = moveHalf - mDimens.getChangeModeStartDistance();
        }

        int showcaseWidth = calculateLayoutSize(showcaseOffset,
                mDimens.getShowcaseNarrowWidth(),
                mDimens.getShowcaseWideWidth());
        int categWidth = calculateLayoutSize(categOffset,
                mDimens.getCategNarrowWidth(),
                mDimens.getCategWideWidth());

        changeLayoutSize(categWidth, showcaseWidth, 0);
    }

    /**
     * Calculate layout size according to touch gesture distance.
     *
     * @param movX    touch distance in pixels
     * @param minSize minimum size of the layout
     * @param maxSize maximum size of the layout
     * @return value for size of layout between minSize and maxSize according to movX
     */
    private int calculateLayoutSize(int movX, int minSize, int maxSize) {
        if (movX <= 0) {
            return minSize;
        } else if (movX < maxSize - minSize) {
            return minSize + movX;
        } else {
            return maxSize;
        }
    }

    /**
     * Set appropriate mode (Basket or Showcase), cancel touch handling and clear the state
     */
    private void finishModeChange() {
        if (isChangeModeHandled && allowChangeMode) {
            mVelocityTracker.computeCurrentVelocity(1000, mDimens.getMaxVelocity());
            float velocity = mVelocityTracker.getXVelocity();
            Interpolator interpolator = getInterpolator(velocity);
            mChangeBounds.setInterpolator(interpolator);
            setMode(movX, velocity);
        }

        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
        allowChangeMode = true;
        isChangeModeHandled = false;
        initX = 0;
        movX = 0;
    }

    /**
     * Gives appropriate animation interpolator according to the gesture velocity
     *
     * @param velocity speed of the user gesture
     * @return appropriate decelerate interpolator
     */
    private Interpolator getInterpolator(float velocity) {
        int factor = (int) (Math.abs(velocity) / CHANGE_MODE_VELOCITY_DIVIDER);
        switch (factor) {
            case 0: {
                return new AccelerateDecelerateInterpolator();
            }
            case 1: {
                // through it
            }
            case 2: {
                return new SmoothDecelerateInterpolator();
            }
            case 3: {
                return new DecelerateInterpolator(1.5f);
            }
            default: {
                return new DecelerateInterpolator(2.5f);
            }
        }
    }

    /**
     * Set basket or showcase mode in depends of the touch moving distance and velocity
     *
     * @param velocity touch moving velocity
     */
    private void setMode(int movX, float velocity) {
        if (mViewModel.isShowcaseMode()) {
            trySetBasketMode(movX, velocity);
        } else {
            trySetShowcaseMode(movX, velocity);
        }
    }

    private void trySetBasketMode(int movX, float velocity) {
        if (velocity < CHANGE_MODE_MIN_VELOCITY && movX < -mDimens.getChangeModeDistance()) {
            setBasketMode();
        } else {
            recoverShowcaseMode(movX);
        }
    }

    private void trySetShowcaseMode(int movX, float velocity) {
        if (velocity > -CHANGE_MODE_MIN_VELOCITY && movX > mDimens.getChangeModeDistance()) {
            setShowcaseMode();
        } else {
            recoverBasketMode(movX);
        }
    }

    private void recoverShowcaseMode(int movX) {
        if (movX < -mDimens.getProtectedInterval()) {
            TransitionManager.beginDelayedTransition(mRootLayout, mChangeModeTransition);
        }
        changeLayoutSize(mDimens.getCategWideWidth(),
                0,
                mDimens.getBasketNarrowWidth());
    }

    private void recoverBasketMode(int movX) {
        if (movX > mDimens.getProtectedInterval()) {
            TransitionManager.beginDelayedTransition(mRootLayout, mChangeModeTransition);
        }
        changeLayoutSize(mDimens.getCategNarrowWidth(),
                mDimens.getShowcaseNarrowWidth(),
                0);
    }

    /**
     * Set current mode to the Basket mode.
     * Mode state is observed, so this method also invoke {@link #applyBasketModeLayout()}
     * which actually resize views to consist the mode.
     */
    private void setBasketMode() {
        if (isLandscapeMode) return;
        mViewModel.setViewMode(false);
    }

    /**
     * Set current mode to the Showcase mode.
     * Mode state is observed, so this method also invoke {@link #applyShowcaseModeLayout()}
     * which actually resize views to consist the mode.
     */
    private void setShowcaseMode() {
        if (isLandscapeMode) return;
        mViewModel.setViewMode(true);
    }

    /**
     * Change size of the layout parts: Categories, Showcase and Basket.
     * If one of the width equal '0' then the corresponding layout part fills in the free space.
     *
     * @param categWidth    width of the Categories in pixels
     * @param showcaseWidth width of the Showcase in pixels
     * @param basketWidth   width of the Basket in pixels
     */
    private void changeLayoutSize(int categWidth, int showcaseWidth, int basketWidth) {
        changeViewWidth(mCategoriesContainer, categWidth);
        changeViewWidth(mShowcaseContainer, showcaseWidth);
        changeViewWidth(mBasketContainer, basketWidth);
    }

    /**
     * Change size of the view.
     * If width equal '0' then view fills in the free space.
     *
     * @param view  view which width need change
     * @param width width of the view in pixels
     */
    private void changeViewWidth(View view, int width) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params.width == width) return;
        params.width = width;
        view.setLayoutParams(params);
    }

    private void initViews() {
        mRootLayout = mActivity.findViewById(R.id.root_layout);
        mCategoriesContainer = mActivity.findViewById(R.id.fragment_categories);
        mShowcaseContainer = mActivity.findViewById(R.id.fragment_showcase);
        mShowcase = mShowcaseContainer.findViewById(R.id.showcase_list);
        mBasketContainer = mActivity.findViewById(R.id.fragment_basket);
        mBasket = mBasketContainer.findViewById(R.id.basket_list);
    }

    private void initTransitions() {
        mChangeBounds = new ChangeBounds();
        mChangeModeTransition = new TransitionSet();
        mChangeModeTransition.setDuration(CHANGE_MODE_TIME)
                .setOrdering(TransitionSet.ORDERING_TOGETHER)
                .addTransition(mChangeBounds);
    }

}