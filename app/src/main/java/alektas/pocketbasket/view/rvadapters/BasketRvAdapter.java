package alektas.pocketbasket.view.rvadapters;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;

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
    private int marginEnd;

    public BasketRvAdapter(Context context, ItemsViewModel model,
                    OnStartDragListener dragListener,
                    ItemSizeProvider itemSizeProvider) {
        super(context, model);
        mContext = context;
        mModel = model;
        mDragListener = dragListener;
        // Fix item width
        // Width depends on configuration (landscape or portrait)
        mItemWidth = itemSizeProvider.getBasketItemWidth();
        // Text margin to avoid overlapping the drag handler
        marginEnd = itemSizeProvider.getBasketTextMarginEnd();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemView = inflater.inflate(R.layout.basket_item_view, parent, false);
        itemView.getLayoutParams().width = mItemWidth;
        itemView.requestLayout();
        BasketItemViewBinding binding = DataBindingUtil.bind(itemView);
        binding.setModel(mModel);
        ViewHolder holder = new ViewHolder(binding);
        holder.mName.setPadding(0, 0, marginEnd, 0);
        return holder;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder viewHolder) {
        super.onViewAttachedToWindow(viewHolder);

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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder viewHolder) {
        super.onViewDetachedFromWindow(viewHolder);

        viewHolder.mDragHandle.setOnTouchListener(null);
        viewHolder.mIconView.setOnClickListener(null);
    }

    // add check image to icon of item in basket if item is checked
    @Override
    void setChooseIcon(ViewHolder viewHolder, Item item) {
        if (mModel.isItemChecked(item.getName())) {
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
