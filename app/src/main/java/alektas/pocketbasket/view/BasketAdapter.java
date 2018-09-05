package alektas.pocketbasket.view;

import android.content.Context;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import alektas.pocketbasket.IPresenter;
import alektas.pocketbasket.R;
import alektas.pocketbasket.model.Data;

public class BasketAdapter extends ItemListAdapter {
    private static final String TAG = "BasketAdapter";
    private Context mContext;
    private IPresenter mPresenter;
    private List<Data> mData;

    public BasketAdapter(Context context, IPresenter presenter) {
        super(context, presenter, presenter.getSelected(), false);
        mContext = context;
        mPresenter = presenter;
        mData = presenter.getSelected();
    }

    class SlideItemListener extends GestureDetector.SimpleOnGestureListener {
        private String mKey;

        SlideItemListener(String itemKey) {
            mKey = itemKey;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (distanceX < -20) mPresenter.deleteData(mKey);
            return true;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = super.getView(position, convertView, parent);
        final Data item = mData.get(position);
        ViewHolder viewHolder = super.getViewHolder();
        final GestureDetector gestureDetector =
                new GestureDetector(mContext, new SlideItemListener(item.getKey()));
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

        viewHolder.mItemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);
                return true;
            }
        });
        return itemView;
    }
}
