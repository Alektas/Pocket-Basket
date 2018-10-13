package alektas.pocketbasket.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import alektas.pocketbasket.R;
import alektas.pocketbasket.db.entity.Item;
import alektas.pocketbasket.viewmodel.ItemsViewModel;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BasketRvAdapter extends RecyclerView.Adapter<BasketRvAdapter.BasketViewHolder> {
    private static final String TAG = "BasketAdapter";
    private final float DEL_EDGE = 0.6f;
    private final float TAP_PADDING;
    private final float CHECKABLE_ZONE;
    private Context mContext;
    private ItemsViewModel mModel;
    private List<Item> mItems;

    BasketRvAdapter(Context context, ItemsViewModel model) {
        mContext = context;
        mModel = model;

        float padding = getPadding();
        float iconSize = getIconSize();
        CHECKABLE_ZONE = 2*padding + iconSize;
        TAP_PADDING = CHECKABLE_ZONE;
    }

    static class BasketViewHolder extends RecyclerView.ViewHolder {
        final View mItemView;
        final ImageView mImage;
        final TextView mIconText;
        final ImageView mCheckImage;
        final TextView mName;

        BasketViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemView = itemView;
            mImage = mItemView.findViewById(R.id.item_image);
            mIconText = mItemView.findViewById(R.id.info_text);
            mCheckImage = mItemView.findViewById(R.id.check_image);
            mName = mItemView.findViewById(R.id.item_name);
        }
    }

    @Override
    public int getItemCount() {
        if (mItems != null) return mItems.size();
        return 0;
    }

    @NonNull
    @Override
    public BasketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_view, parent, false);
        return new BasketViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull BasketViewHolder viewHolder, int position) {
        Item item = mItems.get(position);
        setItemText(viewHolder, item);
        setItemIcon(viewHolder, item);
        setChooseIcon(viewHolder, item);
        viewHolder.mItemView.setOnTouchListener((view, motionEvent) -> {
            ((ViewGroup) view.getParent())
                    .setOnTouchListener(getSwipeListener(view, item.getName()));
            return false; // need to be false to allow sliding at the item view zone
        });
    }

    public void setItems(List<Item> items) {
        mItems = items;
        notifyDataSetChanged();
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

    // hide item name in showcase mode and show in basket mode in "Basket"
    private void setItemText(BasketViewHolder viewHolder, Item item) {
        if (mModel.isBasketNamesShow()) {
            viewHolder.mName.setText(getItemName(item));
        }
        else viewHolder.mName.setText("");
    }

    // set item icon (or name instead)
    private void setItemIcon(BasketViewHolder viewHolder, Item item) {
        if (item.getImgRes() != 0) {
            viewHolder.mImage.setImageResource(item.getImgRes());
            viewHolder.mIconText.setText("");
        } else {
            viewHolder.mImage.setImageResource(0);
            viewHolder.mIconText.setText(getItemName(item));
        }
    }

    // add check image to icon of item in basket if item is checked
    private void setChooseIcon(BasketViewHolder viewHolder, Item item) {
        if (item.isChecked()) {
            viewHolder.mCheckImage.setImageResource(R.drawable.ic_checked);
        }
        else {
            viewHolder.mCheckImage.setImageResource(0);
        }
    }

    // get item name from resources or from key field if res is absent
    private String getItemName(Item item) {
        int nameRes = item.getNameRes();
        if (nameRes == 0) { return item.getName(); }
        try {
            return mContext.getString(nameRes);
        }
        catch (Resources.NotFoundException e) { return item.getName(); }
    }

    private float getPadding() {
        return mContext.getResources().getDimension(R.dimen.padding_8);
    }

    private float getIconSize() {
        return mContext.getResources().getDimension(R.dimen.ic_item_size);
    }
}
