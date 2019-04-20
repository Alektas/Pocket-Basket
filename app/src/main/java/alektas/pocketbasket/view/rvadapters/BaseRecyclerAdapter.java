package alektas.pocketbasket.view.rvadapters;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import alektas.pocketbasket.databinding.BasketItemViewBinding;
import alektas.pocketbasket.databinding.ShowcaseItemViewBinding;
import alektas.pocketbasket.db.entities.Item;
import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseRecyclerAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> mItems;

    BaseRecyclerAdapter() {
        setHasStableIds(true);
    }

    static class ItemHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "ItemHolder";
        private ViewDataBinding mBinding;

        private ItemHolder(@NonNull View itemView) {
            super(itemView);
        }

        ItemHolder(ViewDataBinding binding) {
            this(binding.getRoot());
            mBinding = binding;
        }

        void bind(Item item) {
            if (mBinding instanceof ShowcaseItemViewBinding) {
                ((ShowcaseItemViewBinding) mBinding).setItem(item);
                mBinding.executePendingBindings();
            }
            if (mBinding instanceof BasketItemViewBinding) {
                ((BasketItemViewBinding) mBinding).setItem(item);
                mBinding.executePendingBindings();
            }
        }

        void bind(Item item, RecyclerView.ViewHolder holder) {
            if (mBinding instanceof ShowcaseItemViewBinding) {
                ((ShowcaseItemViewBinding) mBinding).setItem(item);
                ((ShowcaseItemViewBinding) mBinding).setHolder(holder);
                mBinding.executePendingBindings();
            }
            if (mBinding instanceof BasketItemViewBinding) {
                ((BasketItemViewBinding) mBinding).setItem(item);
                ((BasketItemViewBinding) mBinding).setHolder(holder);
                mBinding.executePendingBindings();
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mItems != null) return mItems.size();
        return 0;
    }

    @Override
    public long getItemId(int position) {
        Object obj = mItems.get(position);
        if (obj instanceof Item) return ((Item) obj).getName().hashCode();
        return obj.hashCode();
    }

    @NonNull
    @Override
    public abstract RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Object obj = mItems.get(position);
        if (obj instanceof Item) {
            ItemHolder vh = (ItemHolder) viewHolder;
            Item item = (Item) obj;
            vh.bind(item, viewHolder);
        }
    }

    public List<Object> getItems() {
        return mItems;
    }

    public void setItems(List<Object> items) {
        mItems = items;
        notifyDataSetChanged();
    }

}
