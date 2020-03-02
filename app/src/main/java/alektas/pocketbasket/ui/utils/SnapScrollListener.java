package alektas.pocketbasket.ui.utils;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

public abstract class SnapScrollListener extends RecyclerView.OnScrollListener {
    private SnapHelper mSnapper;
    private int mItemCount;
    private int mLastSpanPosition = RecyclerView.NO_POSITION;

    public SnapScrollListener(SnapHelper snapper, int itemCount) {
        mSnapper = snapper;
        mItemCount = itemCount;
    }

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            int snapPosition = getSnapPosition(recyclerView);
            if (snapPosition != mLastSpanPosition) {
                onSnapChanged(snapPosition);
                mLastSpanPosition = snapPosition;
            }
        }
    }

    public abstract void onSnapChanged(int snapPosition);

    private int getSnapPosition(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        View view = mSnapper.findSnapView(layoutManager);
        if (view == null) return RecyclerView.NO_POSITION;
        int position = recyclerView.getChildAdapterPosition(view);
        return position % mItemCount;
    }
}
