package alektas.pocketbasket.view;

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

public class BasketRvAdapter extends BaseRecyclerAdapter
        implements ItemTouchAdapter {

    private static final String TAG = "BasketAdapter";
    private ItemsViewModel mModel;
    private OnStartDragListener mDragListener;

    BasketRvAdapter(Context context, ItemsViewModel model, OnStartDragListener dragListener) {
        super(context, model);
        mModel = model;
        mDragListener = dragListener;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);

        // Not overrided from BaseRecyclerAdapter because of drag handle needed only in Basket
        viewHolder.mDragHandle.setImageResource(R.drawable.ic_drag_handle_darkgreen_24dp);
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
    public void clearView() {
        mModel.updatePositions(getItems());
    }
}
