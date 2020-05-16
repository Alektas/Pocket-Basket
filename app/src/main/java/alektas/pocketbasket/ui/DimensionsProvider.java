package alektas.pocketbasket.ui;

import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.ViewConfiguration;

import androidx.appcompat.app.AppCompatActivity;

import alektas.pocketbasket.R;

public class DimensionsProvider {
    private AppCompatActivity mActivity;
    private float mMaxVelocity;
    private float mProtectedInterval;
    private float mChangeModeDistance;
    private int mChangeModeStartDistance;
    private int mBottomAppBarY = 0;
    private int mCategNarrowWidth;
    private int mCategWideWidth;
    private int mShowcaseWideWidth;
    private int mShowcaseNarrowWidth;
    private int mBasketNarrowWidth;
    private int mBasketWideWidth;
    private int mShowcaseOffset;
    private int mCategOffset;

    public DimensionsProvider(AppCompatActivity activity) {
        mActivity = activity;
        initDimensions(activity);
    }

    private void initDimensions(AppCompatActivity activity) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
        int screenHeight = displaymetrics.heightPixels;
        int minDisplaySize = Math.min(screenHeight, screenWidth);

        mCategNarrowWidth = (int) activity.getResources().getDimension(R.dimen.width_categ_narrow);
        mShowcaseNarrowWidth = (int) activity.getResources().getDimension(R.dimen.width_showcase_narrow);
        mBasketNarrowWidth = (int) activity.getResources().getDimension(R.dimen.width_basket_narrow);
        mCategWideWidth = (int) activity.getResources().getDimension(R.dimen.width_categ_wide);
        mShowcaseWideWidth = minDisplaySize - mCategWideWidth - mBasketNarrowWidth;
        if (isLandscape(activity)) {
            mBasketWideWidth = screenWidth - mCategWideWidth - mShowcaseWideWidth;
        } else {
            mBasketWideWidth = screenWidth - mCategNarrowWidth - mShowcaseNarrowWidth;
        }

        mChangeModeDistance = activity.getResources().getDimension(R.dimen.change_mode_distance);
        mProtectedInterval = activity.getResources().getDimension(R.dimen.protected_interval);
        mChangeModeStartDistance =
                (int) activity.getResources().getDimension(R.dimen.change_mode_start_distance);

        mShowcaseOffset = mShowcaseWideWidth - mShowcaseNarrowWidth + mChangeModeStartDistance;
        mCategOffset = mCategWideWidth - mCategNarrowWidth + mChangeModeStartDistance;

        mMaxVelocity = ViewConfiguration.get(activity).getScaledMaximumFlingVelocity();
    }
    
    private boolean isLandscape(AppCompatActivity activity) {
        return activity.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
    }


    public float getMaxVelocity() {
        return mMaxVelocity;
    }

    public float getProtectedInterval() {
        return mProtectedInterval;
    }

    public float getChangeModeDistance() {
        return mChangeModeDistance;
    }

    public int getChangeModeStartDistance() {
        return mChangeModeStartDistance;
    }

    public int getBottomAppBarY() {
        // Calculate lazily, becuase at the init stage activity hasn't yet measured it's views
        if (mBottomAppBarY == 0) {
            mBottomAppBarY = (int) mActivity.findViewById(R.id.bottom_appbar).getY();
            int resourceId = mActivity.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                float statusBarHeight = mActivity.getResources().getDimensionPixelSize(resourceId);
                mBottomAppBarY += statusBarHeight; // status bar is not considered in "getY()"
            }
        }
        return mBottomAppBarY;
    }

    public int getCategNarrowWidth() {
        return mCategNarrowWidth;
    }

    public int getCategWideWidth() {
        return mCategWideWidth;
    }

    public int getShowcaseWideWidth() {
        return mShowcaseWideWidth;
    }

    public int getShowcaseNarrowWidth() {
        return mShowcaseNarrowWidth;
    }

    public int getBasketNarrowWidth() {
        return mBasketNarrowWidth;
    }

    public int getBasketWideWidth() {
        return mBasketWideWidth;
    }

    public int getShowcaseMoveRange() {
        return mShowcaseOffset;
    }

    public int getCategMoveRange() {
        return mCategOffset;
    }
}