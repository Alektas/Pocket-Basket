package alektas.pocketbasket.ui.utils;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import alektas.pocketbasket.databinding.ItemBasketBinding;
import alektas.pocketbasket.databinding.ItemShowcaseBinding;
import alektas.pocketbasket.domain.entities.BasketItemModel;
import alektas.pocketbasket.domain.entities.ItemModel;
import alektas.pocketbasket.domain.entities.ShowcaseItemModel;

public abstract class BaseRecyclerAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ItemModel> mItems;

    public BaseRecyclerAdapter() {
        mItems = new ArrayList<>();
        setHasStableIds(true);
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "ItemHolder";
        private ViewDataBinding mBinding;

        private ItemHolder(@NonNull View itemView) {
            super(itemView);
        }

        public ItemHolder(ViewDataBinding binding) {
            this(binding.getRoot());
            mBinding = binding;
        }

        void bind(alektas.pocketbasket.domain.entities.ItemModel item, RecyclerView.ViewHolder holder) {
            if (mBinding instanceof ItemShowcaseBinding) {
                ((ItemShowcaseBinding) mBinding).setItem((ShowcaseItemModel) item);
            }
            if (mBinding instanceof ItemBasketBinding) {
                ((ItemBasketBinding) mBinding).setItem((BasketItemModel) item);
                ((ItemBasketBinding) mBinding).setHolder(holder);
            }
            mBinding.executePendingBindings();
        }
    }

    @Override
    public int getItemCount() {
        if (mItems == null) return 0;
        return mItems.size();
    }

    @Override
    public long getItemId(int position) {
        ItemModel obj = mItems.get(position);
        return obj.getKey().hashCode();
    }

    @NonNull
    @Override
    public abstract RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ItemModel obj = mItems.get(position);
        ItemHolder vh = (ItemHolder) viewHolder;
        vh.bind(obj, viewHolder);
    }

    public List<ItemModel> getItems() {
        return mItems;
    }

    public void setItems(List<ItemModel> newItems) {
        if (newItems.size() < 2) {
            mItems = newItems;
            notifyDataSetChanged();
            return;
        }
        DiffUtil.DiffResult result =
                DiffUtil.calculateDiff(new ItemsDiffCallback(mItems, newItems));
        mItems = newItems;
        result.dispatchUpdatesTo(this);
    }

}
