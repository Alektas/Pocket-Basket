package alektas.pocketbasket.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

import alektas.pocketbasket.R;
import alektas.pocketbasket.db.entity.Item;
import alektas.pocketbasket.viewmodel.ItemsViewModel;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class BasketRvAdapter extends BaseRecyclerAdapter {
    private static final String TAG = "BasketAdapter";
    private final float DEL_DISTANCE;
    private final float CHECKABLE_ZONE;
    private Context mContext;
    private ItemsViewModel mModel;
    private float mTouchX;
    private boolean mItemColored;
    private int mItemBgColor;
    private int mItemBgDelColor;

    BasketRvAdapter(Context context, ItemsViewModel model) {
        super(context, model);
        mContext = context;
        mModel = model;

        float padding = getPadding();
        float iconSize = getIconSize();
        CHECKABLE_ZONE = 2*padding + iconSize;
        DEL_DISTANCE = getDelDistance();

        mItemBgColor = ContextCompat.getColor(mContext, R.color.item_bg);
        mItemBgDelColor = ContextCompat.getColor(mContext, R.color.item_bg_del);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        Item item = getItems().get(position);
        viewHolder.mItemView.setOnTouchListener((view, motionEvent) -> {
            ((ViewGroup) view.getParent())
                    .setOnTouchListener(getSwipeListener(view, item));
            return false; // need to be false to allow sliding at the item view zone
        });
    }

    // hide item name in showcase mode and show in basket mode in "Basket"
    @Override
    void setItemText(ViewHolder viewHolder, Item item) {
        super.setItemText(viewHolder, item);
        if (mModel.isBasketNamesShow()) {
            viewHolder.mName.setVisibility(View.VISIBLE);
        }
        else viewHolder.mName.setVisibility(View.GONE);
    }

    // add check image to icon of item in basket if item is checked
    @Override
    void setChooseIcon(ViewHolder viewHolder, Item item) {
        if (item.isChecked()) {
            viewHolder.mCheckImage.setImageResource(R.drawable.ic_checked);
        }
        else {
            viewHolder.mCheckImage.setImageResource(0);
        }
    }

    // ListView listener for processing items sliding and check
    private View.OnTouchListener getSwipeListener(final View itemView, final Item item) {
        return (parentView, event) -> {
            parentView.onTouchEvent(event); // for enable list view scrolling
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mTouchX = event.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (event.getX() > mTouchX) {
                        itemView.setX(event.getX() - mTouchX);

                        if ( (itemView.getX() - mTouchX) > DEL_DISTANCE) {
                            if (!mItemColored) {
                                paintView(itemView, true);
                            }
                        }
                        else {
                            if (mItemColored) {
                                paintView(itemView, false);
                            }
                        }

                    }
                    else {
                        itemView.setX(0);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    moveViewBack(itemView);
                    parentView.setOnTouchListener(null);
                    itemView.performClick();
                    break;
                case MotionEvent.ACTION_UP:
                    if ( (itemView.getX() - mTouchX) > DEL_DISTANCE) {
                        removeItem(itemView, item);
                    }
                    else {
                        moveViewBack(itemView);
                    }
                    if (event.getX() < CHECKABLE_ZONE
                            && event.getX() > 0
                            && event.getY() > itemView.getY()
                            && event.getY() < itemView.getY() + CHECKABLE_ZONE) {
                        mModel.checkItem(item);
                        itemView.performClick();
                    }
                    // remove listener from parent to avoid unnecessary swiping
                    parentView.setOnTouchListener(null);
                    break;
            }
            return false;
        };
    }

    private void removeItem(final View itemView, final Item item) {
        ValueAnimator fadeAnim = ValueAnimator.ofFloat(itemView.getX(),
                ((ViewGroup)itemView.getParent()).getWidth());
        fadeAnim.setInterpolator(new AccelerateInterpolator());
        fadeAnim.addUpdateListener(valueAnimator ->
                itemView.setX((float) valueAnimator.getAnimatedValue()));

        fadeAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mModel.removeBasketItem(item);
                itemView.setX(0); // TODO: need solution to avoid item blinking on deleting
                itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.item_bg));
            }
        });

        fadeAnim.setDuration(200);
        fadeAnim.start();
    }

    private void moveViewBack(View itemView) {
        ObjectAnimator.ofFloat(itemView, View.X,
                itemView.getX(), 0)
                .setDuration(200).start();
        if (mItemColored) {
            paintView(itemView, false);
        }
    }

    private void paintView(View view, boolean colorful) {
        mItemColored = colorful;
        if (colorful) {
            runColorAnim(view, mItemBgColor, mItemBgDelColor);
        }
        else {
            runColorAnim(view, mItemBgDelColor, mItemBgColor);
        }
    }

    private void runColorAnim(View view, int fromColor, int toColor) {
        ValueAnimator animation = ValueAnimator.ofObject(
                new android.animation.ArgbEvaluator(),
                fromColor,
                toColor);
        animation.addUpdateListener(updatedAnimation -> {
            int animatedValue = (int)updatedAnimation.getAnimatedValue();
            view.setBackgroundColor(animatedValue);
        });
        animation.setDuration(200);
        animation.start();
    }


    private float getPadding() {
        return mContext.getResources().getDimension(R.dimen.padding_8);
    }

    private float getIconSize() {
        return mContext.getResources().getDimension(R.dimen.ic_item_size);
    }

    private float getDelDistance() {
        return mContext.getResources().getDimension(R.dimen.ic_item_size);
    }
}
