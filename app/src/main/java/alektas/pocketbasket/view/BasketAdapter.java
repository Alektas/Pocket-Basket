package alektas.pocketbasket.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import alektas.pocketbasket.IPresenter;
import alektas.pocketbasket.R;
import alektas.pocketbasket.model.Data;

public class BasketAdapter extends BaseAdapter {
    private static final String TAG = "CURRENT_APP_LOG";
    private final float EDGE = 0.6f;
    private final float TAP_PADDING = 0f;
    private final float VIEW_PADDING = 0f;
    private Context mContext;
    private IPresenter mPresenter;
    private List<Data> mData;

    public BasketAdapter(Context context, IPresenter presenter) {
        mContext = context;
        mPresenter = presenter;
        mData = presenter.getSelected();
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
        return mData.size();
    }

    public Object getItem(int position) {
        return mData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ClickableViewAccessibility")
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout itemSlideWrapper = (RelativeLayout) convertView;

        ViewHolder viewHolder;
        if (itemSlideWrapper == null) {
            itemSlideWrapper = (RelativeLayout) initView(parent);
            viewHolder = new ViewHolder(itemSlideWrapper);
            itemSlideWrapper.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // get item and it's icon res and name
        final Data item = mData.get(position);
        int imgRes = item.getImgRes();
        String itemName = getItemName(item);

        // hide item name in showcase mode and show in basket mode in "Basket"
        if (!mPresenter.isShowcaseMode()) {
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

        // TODO: 07.09.2018 need to change gesture detector with click listener for checking items
        final GestureDetector gestureDetector =
                new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener());
        gestureDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
                mPresenter.checkItem(item.getKey());
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent motionEvent) {
                mPresenter.deleteData(item.getKey());
                return true;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent motionEvent) {
                return false;
            }
        });

        final View slidingView = itemSlideWrapper.getChildAt(0);
        final float wrapperWidth = parent.getWidth();
        itemSlideWrapper.
                setOnTouchListener(getSwipeListener(slidingView, item.getKey(), wrapperWidth));

        return itemSlideWrapper;
    }

    // inflate item View from resources and put it into wrapper for sliding
    // or get ViewHolder from save if exist
    private View initView(ViewGroup parent) {
        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View itemView = inflater.inflate(R.layout.item_view, parent, false);

        RelativeLayout itemSlideWrapper = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams layoutParamsItem = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsItem.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        itemSlideWrapper.addView(itemView, layoutParamsItem);

        return itemSlideWrapper;
    }

    // get item name from resources or from key field if res is absent
    private String getItemName(Data item) {
        String itemName;
        int nameRes = item.getNameRes();
        try {
            itemName = mContext.getString(nameRes);
        }
        catch (Resources.NotFoundException e) {
            itemName = item.getKey();
        }
        return itemName;
    }

    @SuppressLint("ClickableViewAccessibility")
    private View.OnTouchListener getSwipeListener(final View itemView,
                                                  final String itemKey,
                                                  final float parentWidth) {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                gestureDetector.onTouchEvent(event);

                final float[] initialX = {0};
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX[0] = event.getX();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        if (event.getX() > VIEW_PADDING + TAP_PADDING) {
                            itemView.setX(event.getX() - initialX[0] - TAP_PADDING);
                        }
                        if (event.getX() < TAP_PADDING) {
                            itemView.setX(VIEW_PADDING);
                        }
                        return true;
                    case MotionEvent.ACTION_CANCEL:
                        moveViewBack(itemView, VIEW_PADDING);
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (itemView.getX() > parentWidth * EDGE) {
                            removeItem(itemView, itemKey);
                        }
                        else {
                            moveViewBack(itemView, VIEW_PADDING);
                        }
                        return true;
                }
                return false;
            }
        };
    }

    private void removeItem(final View view, final String key) {
        ValueAnimator fadeAnim = ValueAnimator.ofFloat(1, 0);
        fadeAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setAlpha((float) valueAnimator.getAnimatedValue());
            }
        });

        fadeAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setX(VIEW_PADDING);
                view.setAlpha(1f);
                mPresenter.deleteData(key);
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
}
