package alektas.pocketbasket.ui.basket;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;

import alektas.pocketbasket.R;
import alektas.pocketbasket.databinding.ItemBasketBinding;
import alektas.pocketbasket.domain.entities.ItemModel;
import alektas.pocketbasket.ui.ItemSizeProvider;
import alektas.pocketbasket.ui.utils.BaseRecyclerAdapter;

public class BasketRvAdapter extends BaseRecyclerAdapter
        implements ItemTouchAdapter {

    private static final String TAG = "BasketAdapter";
    private Context mContext;
    private BasketViewModel mModel;
    private OnStartDragListener mDragListener;
    private ItemSizeProvider mSizeProvider;

    public BasketRvAdapter(Context context, @NonNull BasketViewModel model,
                    OnStartDragListener dragListener) {
        super();
        mContext = context;
        mModel = model;
        mDragListener = dragListener;
    }

    public BasketRvAdapter(Context context, @NonNull BasketViewModel model,
                           OnStartDragListener dragListener,
                           ItemSizeProvider itemSizeProvider) {
        super();
        mContext = context;
        mModel = model;
        mDragListener = dragListener;
        // Fix item width
        // Width depends on configuration (landscape or portrait)
        mSizeProvider = itemSizeProvider;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.item_basket, parent, false);
        itemView.getLayoutParams().width = mSizeProvider.getBasketItemWidth();
        itemView.requestLayout();
        ItemBasketBinding binding = DataBindingUtil.bind(itemView);
        binding.setModel(mModel);
        binding.setDragListener(mDragListener);
        return new ItemHolder(binding);
    }

    @Override
    public void onItemDismiss(int position) {
        mModel.removeFromBasket(((ItemModel) getItems().get(position)).getName());
    }

    @Override
    public void onSwipeStart(RecyclerView.ViewHolder viewHolder) {
        try {
            runColorAnim(viewHolder.itemView, true);
        } catch (ClassCastException e) {
            Log.e(TAG, "onSwipeStart: viewHolder must be from BaseRecyclerAdapter", e);
        }
    }

    @Override
    public void onSwipeEnd(RecyclerView.ViewHolder viewHolder) {
        try {
            runColorAnim(viewHolder.itemView, false);
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
    public void onItemMoveEnd() {
        mModel.updatePositions(getItems());
    }

    private void runColorAnim(View itemView, boolean colorful) {
        int animId;
        if (colorful) { animId = R.animator.anim_colorful; }
        else { animId = R.animator.anim_colorless; }
        Animator anim = AnimatorInflater.loadAnimator(mContext, animId);
        anim.setTarget(itemView);
        anim.start();
    }
}
