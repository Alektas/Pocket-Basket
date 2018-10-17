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

import alektas.pocketbasket.R;
import alektas.pocketbasket.db.entity.Item;
import alektas.pocketbasket.viewmodel.ItemsViewModel;

public class BasketAdapter extends BaseItemAdapter {
    private static final String TAG = "BasketAdapter";
    private final float DEL_EDGE = 0.6f;
    private final float TAP_PADDING;
    private final float CHECKABLE_ZONE;
    private Context mContext;
    private ItemsViewModel mModel;

    BasketAdapter(Context context, ItemsViewModel model) {
        super(context);
        mContext = context;
        mModel = model;

        float padding = getPadding();
        float iconSize = getIconSize();
        CHECKABLE_ZONE = 2*padding + iconSize;
        TAP_PADDING = CHECKABLE_ZONE;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        View itemView = super.getView(position, convertView, parent);
        itemView.setOnTouchListener((view, motionEvent) -> {
            parent.setOnTouchListener(
                    getSwipeListener(view, ((Item)getItem(position)).getName()));
            return false; // need to be false to allow sliding at the item view zone
        });

        return itemView;
    }

    // hide item name in showcase mode and show in basket mode in "Basket"
    @Override
    void setItemText(ViewHolder viewHolder, Item item) {
        if (mModel.isBasketNamesShow()) {
            viewHolder.mName.setText(getItemName(item));
        }
        else viewHolder.mName.setText("");
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
    @SuppressLint("ClickableViewAccessibility")
    private View.OnTouchListener getSwipeListener(final View itemView, final String itemKey ) {
        return (v, event) -> {
            v.onTouchEvent(event); // for enable list view scrolling

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    return true;
                case MotionEvent.ACTION_MOVE:
                    if (event.getX() > TAP_PADDING) {
                        itemView.setX(event.getX() - TAP_PADDING);
                    }
                    else {
                        itemView.setX(0);
                    }
                    return true;
                case MotionEvent.ACTION_CANCEL:
                    moveViewBack(itemView);
                    return true;
                case MotionEvent.ACTION_UP:
                    if (itemView.getX() > v.getWidth() * DEL_EDGE) {
                        removeItem(itemView, itemKey);
                    }
                    else {
                        moveViewBack(itemView);
                    }
                    if (event.getX() < CHECKABLE_ZONE && event.getX() > 0) {
                        mModel.checkItem(itemKey);
                    }
                    v.setOnTouchListener((view, motionEvent) -> false);
                    return true;
            }
            return false;
        };
    }

    private void removeItem(final View view, final String key) {
        ValueAnimator fadeAnim = ValueAnimator.ofFloat(view.getX(),
                ((ViewGroup)view.getParent()).getWidth());
        fadeAnim.addUpdateListener(valueAnimator ->
                view.setX((float) valueAnimator.getAnimatedValue()));

        fadeAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mModel.removeBasketItem(key);
                view.setX(0);
            }
        });

        fadeAnim.setDuration(200);
        fadeAnim.start();
    }

    private void moveViewBack(View view) {
        ObjectAnimator.ofFloat(view, View.X,
                view.getX(), 0)
                .setDuration(200).start();
    }

    private float getPadding() {
        return mContext.getResources().getDimension(R.dimen.padding_8);
    }

    private float getIconSize() {
        return mContext.getResources().getDimension(R.dimen.ic_item_size);
    }
}
