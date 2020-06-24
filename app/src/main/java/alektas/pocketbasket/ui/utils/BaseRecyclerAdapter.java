package alektas.pocketbasket.ui.utils;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import alektas.pocketbasket.data.db.models.BasketItemDbo;
import alektas.pocketbasket.data.db.models.ShowcaseItemDbo;
import alektas.pocketbasket.databinding.ItemBasketBinding;
import alektas.pocketbasket.databinding.ItemShowcaseBinding;
import alektas.pocketbasket.domain.entities.BasketItem;
import alektas.pocketbasket.domain.entities.IItemModel;
import alektas.pocketbasket.domain.entities.ShowcaseItem;

public abstract class BaseRecyclerAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> mItems;

    public BaseRecyclerAdapter() {
        mItems = new ArrayList<>();
        setHasStableIds(true);
    }

    public static class ShowcaseItemHolder extends RecyclerView.ViewHolder {
        private ItemShowcaseBinding mBinding;

        private ShowcaseItemHolder(@NonNull View itemView) {
            super(itemView);
        }

        public ShowcaseItemHolder(ItemShowcaseBinding binding) {
            this(binding.getRoot());
            mBinding = binding;
        }

        void bind(ShowcaseItem item) {
            mBinding.setItem(item);
            mBinding.executePendingBindings();
        }
    }

    public static class BasketItemHolder extends RecyclerView.ViewHolder {
        private ItemBasketBinding mBinding;

        private BasketItemHolder(@NonNull View itemView) {
            super(itemView);
        }

        public BasketItemHolder(ItemBasketBinding binding) {
            this(binding.getRoot());
            mBinding = binding;
        }

        void bind(BasketItem item) {
            mBinding.setItem(item);
            mBinding.setHolder(this);
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
        Object obj = mItems.get(position);
        if (obj instanceof IItemModel) {
            return ((IItemModel) obj).getKey().hashCode();
        }
        return obj.hashCode();
    }

    @NonNull
    @Override
    public abstract RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Object obj = mItems.get(position);
        if (obj instanceof BasketItem) {
            BasketItemHolder vh = (BasketItemHolder) viewHolder;
            vh.bind((BasketItem) obj);
        }
        if (obj instanceof ShowcaseItem) {
            ShowcaseItemHolder vh = (ShowcaseItemHolder) viewHolder;
            vh.bind((ShowcaseItem) obj);
        }
    }

    public List<Object> getItems() {
        return mItems;
    }

    public void setItems(List<Object> newItems) {
        if (newItems.size() < 2) {
            mItems.clear();
            mItems.addAll(newItems);
            notifyDataSetChanged();
            return;
        }
        DiffUtil.DiffResult result =
                DiffUtil.calculateDiff(new ItemsDiffCallback(mItems, newItems));
        mItems.clear();
        mItems.addAll(newItems);
        result.dispatchUpdatesTo(this);
    }

    public interface ViewRenderListener {
        void onRenderComplete();
    }

    public void setItems(List<Object> newItems, ViewRenderListener renderListener) {
        setItems(newItems);
        if (renderListener != null) renderListener.onRenderComplete();
    }

}
