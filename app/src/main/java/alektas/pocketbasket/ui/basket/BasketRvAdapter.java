package alektas.pocketbasket.ui.basket;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
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
    private Animator mRemoveOnAnimator;
    private Animator mRemoveOffAnimator;
    private Animator mMoveOnAnimator;
    private Animator mMoveOffAnimator;

    public BasketRvAdapter(Context context, @NonNull BasketViewModel model,
                           OnStartDragListener dragListener) {
        super();
        mContext = context;
        mModel = model;
        mDragListener = dragListener;
        mRemoveOnAnimator = AnimatorInflater.loadAnimator(mContext, R.animator.anim_item_remove_on);
        mRemoveOffAnimator = AnimatorInflater.loadAnimator(mContext, R.animator.anim_item_remove_on);
        mMoveOnAnimator = AnimatorInflater.loadAnimator(mContext, R.animator.anim_item_move_on);
        mMoveOffAnimator = AnimatorInflater.loadAnimator(mContext, R.animator.anim_item_move_off);
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

        mRemoveOnAnimator = AnimatorInflater.loadAnimator(mContext, R.animator.anim_item_remove_on);
        mRemoveOffAnimator = AnimatorInflater.loadAnimator(mContext, R.animator.anim_item_remove_off);
        mMoveOnAnimator = AnimatorInflater.loadAnimator(mContext, R.animator.anim_item_move_on);
        mMoveOffAnimator = AnimatorInflater.loadAnimator(mContext, R.animator.anim_item_move_off);
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
        mRemoveOnAnimator.setTarget(viewHolder.itemView);
        mRemoveOnAnimator.start();
    }

    @Override
    public void onSwipeEnd(RecyclerView.ViewHolder viewHolder) {
        mRemoveOffAnimator.setTarget(viewHolder.itemView);
        mRemoveOffAnimator.start();
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
    public void onItemMoveStart(RecyclerView.ViewHolder viewHolder) {
        mMoveOnAnimator.setTarget(viewHolder.itemView);
        mMoveOnAnimator.start();
    }

    @Override
    public void onItemMoveEnd(RecyclerView.ViewHolder viewHolder) {
        mMoveOffAnimator.setTarget(viewHolder.itemView);
        mMoveOffAnimator.start();
        mModel.updatePositions(getItems());
    }
}
