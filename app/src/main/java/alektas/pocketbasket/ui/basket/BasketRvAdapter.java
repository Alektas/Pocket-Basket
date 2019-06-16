package alektas.pocketbasket.ui.basket;

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
        this(context, model, dragListener);
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
        mModel.removeFromBasket(getItems().get(position).getKey());
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(getItems(), fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemMoveEnd(RecyclerView.ViewHolder viewHolder) {
        mModel.updatePositions(getItems());
    }
}
