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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import alektas.pocketbasket.R;
import alektas.pocketbasket.db.entity.Item;
import alektas.pocketbasket.viewmodel.ItemsViewModel;

public class BasketAdapter extends BaseAdapter {
    private static final String TAG = "BasketAdapter";
    private final float DEL_EDGE = 0.6f;
    private final float TAP_PADDING;
    private final float CHECKABLE_ZONE;
    private final float VIEW_PADDING;
    private Context mContext;
    private ItemsViewModel mModel;
    private List<Item> mItems;

    BasketAdapter(Context context, ItemsViewModel model) {
        mContext = context;
        mModel = model;

        float padding = getPadding();
        float iconSize = getIconSize();
        VIEW_PADDING = padding;
        CHECKABLE_ZONE = 2*padding + iconSize;
        TAP_PADDING = CHECKABLE_ZONE;
    }

    static class ViewHolder {
        final View mItemView;
        final ImageView mImage;
        final TextView mText;
        final ImageView mCheckImage;
        final TextView mName;

        ViewHolder(View view) {
            mItemView = view;
            mImage = mItemView.findViewById(R.id.item_image);
            mText = mItemView.findViewById(R.id.info_text);
            mCheckImage = mItemView.findViewById(R.id.check_image);
            mName = mItemView.findViewById(R.id.item_name);
        }
    }

    public int getCount() {
        if (mItems != null) return mItems.size();
        return 0;
    }

    public Object getItem(int position) {
        return mItems.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ClickableViewAccessibility")
    public View getView(int position, View convertView, final ViewGroup parent) {
        View itemView = convertView;

        ViewHolder viewHolder;
        if (itemView == null) {
            itemView = initView(parent);
            viewHolder = new ViewHolder(itemView);
            itemView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Item item = mItems.get(position);
        bindViewWithData(viewHolder, item);

        itemView.setOnTouchListener((view, motionEvent) -> {
            parent.setOnTouchListener(getSwipeListener(view, item.getName()));
            return false; // need to be false to allow sliding at the item view zone
        });

        return itemView;
    }

    public void setItems(List<Item> items) {
        mItems = items;
        notifyDataSetChanged();
        Log.d(TAG, "setItems: basket is notified");
    }

    // inflate item View from resources
    // or get ViewHolder from saves if exist
    private View initView(ViewGroup parent) {
        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        return inflater.inflate(R.layout.item_view, parent, false);
    }

    private void bindViewWithData(ViewHolder viewHolder, Item item) {
        // get item's icon res and name
        int imgRes = item.getImgRes();
        String itemName = getItemName(item);

        // hide item name in showcase mode and show in basket mode in "Basket"
        if (!mModel.isShowcaseMode()) {
            viewHolder.mName.setText(itemName);
        }
        else viewHolder.mName.setText("");

        // set item icon (or name instead)
        if (imgRes > 0) {
            viewHolder.mImage.setImageResource(imgRes);
            viewHolder.mText.setText("");
        } else {
            viewHolder.mImage.setImageResource(0);
            viewHolder.mText.setText(itemName);
        }

        // add check image to icon of item in basket if item is checked
        if (item.isChecked()) {
            viewHolder.mCheckImage.setImageResource(R.drawable.ic_checked);
        }
        else {
            viewHolder.mCheckImage.setImageResource(0);
        }
    }

    // get item name from resources or from key field if res is absent
    private String getItemName(Item item) {
        String itemName;
        int nameRes = item.getNameRes();
        try {
            itemName = mContext.getString(nameRes);
        }
        catch (Resources.NotFoundException e) {
            itemName = item.getName();
        }
        return itemName;
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
                    if (event.getX() > VIEW_PADDING + TAP_PADDING) {
                        itemView.setX(event.getX() - TAP_PADDING);
                    }
                    else {
                        itemView.setX(VIEW_PADDING);
                    }
                    return true;
                case MotionEvent.ACTION_CANCEL:
                    moveViewBack(itemView, VIEW_PADDING);
                    return true;
                case MotionEvent.ACTION_UP:
                    if (itemView.getX() > v.getWidth() * DEL_EDGE) {
                        removeItem(itemView, itemKey);
                    }
                    else {
                        moveViewBack(itemView, VIEW_PADDING);
                    }
                    if (event.getX() < CHECKABLE_ZONE && event.getX() > VIEW_PADDING) {
                        mModel.checkItem(itemKey);
                    }
                    v.setOnTouchListener((view, motionEvent) -> false);
                    return true;
            }
            return false;
        };
    }

    private void removeItem(final View view, final String key) {
        ValueAnimator fadeAnim = ValueAnimator.ofFloat(1, 0);
        fadeAnim.addUpdateListener(valueAnimator ->
                view.setAlpha((float) valueAnimator.getAnimatedValue()));

        fadeAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setX(VIEW_PADDING);
                view.setAlpha(1f);
                mModel.deleteItem(key);
            }
        });

        fadeAnim.setDuration(200);
        fadeAnim.start();
    }

    private void moveViewBack(View view, float toX) {
        ObjectAnimator.ofFloat(view, View.X,
                view.getX(), toX)
                .setDuration(200).start();
    }

    private float getPadding() {
        return mContext.getResources().getDimension(R.dimen.padding);
    }

    private float getIconSize() {
        return mContext.getResources().getDimension(R.dimen.item_ic_size);
    }
}
