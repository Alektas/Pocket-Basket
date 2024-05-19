package alektas.pocketbasket.ui.basket;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import alektas.pocketbasket.R;

public class ItemTouchCallback extends ItemTouchHelper.Callback {
    private ItemTouchAdapter mAdapter;
    private boolean isMove = false;
    private Animator mRemoveOnAnimator;
    private Animator mClearViewAnimator;
    private Animator mMoveOnAnimator;

    public ItemTouchCallback(Context context, ItemTouchAdapter adapter) {
        mAdapter = adapter;
        mRemoveOnAnimator = AnimatorInflater.loadAnimator(context, R.animator.anim_item_remove_on);
        mClearViewAnimator = AnimatorInflater.loadAnimator(context, R.animator.anim_item_clear_view);
        mMoveOnAnimator = AnimatorInflater.loadAnimator(context, R.animator.anim_item_move_on);
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && isCurrentlyActive) {
            final float alpha = 0.6f * (1f - Math.abs(dX) / (float) viewHolder.itemView.getWidth());
            viewHolder.itemView.setAlpha(alpha);
            viewHolder.itemView.setTranslationX(dX);
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (viewHolder == null) return;

        if (ItemTouchHelper.ACTION_STATE_SWIPE == actionState) {
            mRemoveOnAnimator.setTarget(viewHolder.itemView);
            mRemoveOnAnimator.start();
        } else if (ItemTouchHelper.ACTION_STATE_DRAG == actionState) {
            mMoveOnAnimator.setTarget(viewHolder.itemView);
            mMoveOnAnimator.start();
            isMove = true;
        }
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        mRemoveOnAnimator.cancel();
        mMoveOnAnimator.cancel();

        // If method is called too frequently it's need to finish
        // previous view clearing before setting a new target
        mClearViewAnimator.end();
        mClearViewAnimator.setTarget(viewHolder.itemView);
        mClearViewAnimator.start();

        if (isMove) {
            mAdapter.onItemMoveEnd(viewHolder);
            isMove = false;
        }
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }
}
