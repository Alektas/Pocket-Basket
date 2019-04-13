package alektas.pocketbasket.view.rvadapters;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import alektas.pocketbasket.R;
import alektas.pocketbasket.databinding.BasketItemViewBinding;
import alektas.pocketbasket.db.entities.Item;
import alektas.pocketbasket.view.ItemSizeProvider;
import alektas.pocketbasket.view.ItemTouchAdapter;
import alektas.pocketbasket.view.OnStartDragListener;
import alektas.pocketbasket.viewmodel.ItemsViewModel;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class BasketRvAdapter extends BaseRecyclerAdapter
        implements ItemTouchAdapter {

    private static final String TAG = "BasketAdapter";
    private Context mContext;
    private ItemsViewModel mModel;
    private OnStartDragListener mDragListener;
    private final int mItemWidth;

    public BasketRvAdapter(Context context, @NonNull ItemsViewModel model,
                    OnStartDragListener dragListener,
                    ItemSizeProvider itemSizeProvider) {
        super();
        mContext = context;
        mModel = model;
        mDragListener = dragListener;
        // Fix item width
        // Width depends on configuration (landscape or portrait)
        mItemWidth = itemSizeProvider.getBasketItemWidth();
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.basket_item_view, parent, false);
        itemView.getLayoutParams().width = mItemWidth;
        itemView.requestLayout();
        BasketItemViewBinding binding = DataBindingUtil.bind(itemView);
        binding.setModel(mModel);
        binding.setDragListener(mDragListener);
        return new ItemHolder(binding);
    }

    @Override
    public void onItemDismiss(int position) {
        mModel.removeFromBasket(((Item) getItems().get(position)).getName());
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
    public void onMoveEnd() {
        List<Item> items = new ArrayList<>();
        for (Object obj : getItems()) {
            if (obj instanceof Item) items.add((Item) obj);
        }
        mModel.updatePositions(items);
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
