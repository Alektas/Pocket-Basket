package alektas.pocketbasket.view;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Collections;

import alektas.pocketbasket.R;
import alektas.pocketbasket.db.entity.Item;
import alektas.pocketbasket.viewmodel.ItemsViewModel;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BasketRvAdapter extends BaseRecyclerAdapter
        implements ItemTouchAdapter {

    private static final String TAG = "BasketAdapter";
    private Context mContext;
    private ItemsViewModel mModel;
    private OnStartDragListener mDragListener;

    BasketRvAdapter(Context context, ItemsViewModel model, OnStartDragListener dragListener) {
        super(context, model);
        mContext = context;
        mModel = model;
        mDragListener = dragListener;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);

        /* Not overrided from BaseRecyclerAdapter because of
        drag handle and white background needed only in Basket */
        viewHolder.mDragHandle.setImageResource(R.drawable.ic_drag_handle_darkgreen_24dp);
        viewHolder.mItemView.setBackgroundColor(mContext.getResources().getColor(R.color.item_bg));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder viewHolder) {
        super.onViewAttachedToWindow(viewHolder);

        if (viewHolder != null) {
            viewHolder.mDragHandle.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mDragListener.onStartDrag(viewHolder);
                }
                return false;
            });

            viewHolder.mIconView.setOnClickListener(v -> {
                mModel.checkItem(getItems().get(viewHolder.getAdapterPosition()).getName());
            });
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder viewHolder) {
        super.onViewDetachedFromWindow(viewHolder);

        viewHolder.mDragHandle.setOnTouchListener(null);
        viewHolder.mIconView.setOnClickListener(null);
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
        if (mModel.isChecked(item.getName())) {
            viewHolder.mCheckImage.setImageResource(R.drawable.ic_checked);
        }
        else {
            viewHolder.mCheckImage.setImageResource(0);
        }
    }

    @Override
    public void onItemDismiss(int position) {
        mModel.removeFromBasket(getItems().get(position).getName());
    }

    @Override
    public void onSwipeStart(RecyclerView.ViewHolder viewHolder) {
        try {
            runColorAnim(((ViewHolder) viewHolder).mItemView, true);
        } catch (ClassCastException e) {
            Log.e(TAG, "onSwipeStart: viewHolder must be from BaseRecyclerAdapter", e);
        }
    }

    @Override
    public void onSwipeEnd(RecyclerView.ViewHolder viewHolder) {
        try {
            runColorAnim(((ViewHolder) viewHolder).mItemView, false);
        } catch (ClassCastException e) {
            Log.e(TAG, "onSwipeEnd: viewHolder must be from BaseRecyclerAdapter", e);
        }
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(getItems(), i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(getItems(), i, i - 1);
            }
        }

        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onMoveEnd() {
        mModel.updatePositions(getItems());
    }

    private void runColorAnim(View itemView, boolean colorful) {
        int animId;
        if (colorful) { animId = R.animator.colorful_anim; }
        else { animId = R.animator.colorless_anim; }
        Animator anim = AnimatorInflater.loadAnimator(mContext, animId);
        anim.setTarget(itemView);
        anim.start();
    }
}
