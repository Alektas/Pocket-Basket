package alektas.pocketbasket.view.rvadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import alektas.pocketbasket.R;
import alektas.pocketbasket.databinding.ItemViewBinding;
import alektas.pocketbasket.databinding.BasketItemViewBinding;
import alektas.pocketbasket.db.entities.Item;
import alektas.pocketbasket.viewmodel.ItemsViewModel;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseRecyclerAdapter
        extends RecyclerView.Adapter<BaseRecyclerAdapter.ViewHolder> {
    private Context mContext;
    private ItemsViewModel mModel;
    private List<Item> mItems;

    BaseRecyclerAdapter(Context context, ItemsViewModel model) {
        mContext = context;
        mModel = model;
        setHasStableIds(true);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "ViewHolder";
        final View mItemView;
        final CardView mIconView;
        final ImageView mCheckImage;
        final ImageView mDelImage;
        final ImageView mDragHandle;
        final TextView mName;

        private ItemViewBinding mItemBinding;
        private BasketItemViewBinding mBasketItemBinding;

        ViewHolder(View view) {
            super(view);
            mItemView = itemView;
            mIconView = mItemView.findViewById(R.id.item_icon_view);
            mCheckImage = mItemView.findViewById(R.id.check_image);
            mName = mItemView.findViewById(R.id.item_name);
            mDelImage = mItemView.findViewById(R.id.del_image);
            mDragHandle = mItemView.findViewById(R.id.drag_handle);
        }

        ViewHolder(@NonNull ItemViewBinding binding) {
            this(binding.getRoot());
            mItemBinding = binding;
        }

        ViewHolder(@NonNull BasketItemViewBinding binding) {
            this(binding.getRoot());
            mBasketItemBinding = binding;
        }

        void bind(Item item) {
            if (mItemBinding != null) {
                mItemBinding.setItem(item);
                mItemBinding.executePendingBindings();
            }
            if (mBasketItemBinding != null) {
                mBasketItemBinding.setItem(item);
                mBasketItemBinding.executePendingBindings();
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
        return mItems.get(position).getName().hashCode();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemView = inflater.inflate(R.layout.item_view, parent, false);
        ItemViewBinding binding = DataBindingUtil.bind(itemView);
        binding.setModel(mModel);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Item item = mItems.get(position);
        viewHolder.bind(item);
        setItemText(viewHolder, item);
        setChooseIcon(viewHolder, item);
    }

    public List<Item> getItems() {
        return mItems;
    }

    public void setItems(List<Item> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    void setItemText(ViewHolder viewHolder, Item item) {
        viewHolder.mName.setText(item.getName());
    }

    abstract void setChooseIcon(ViewHolder viewHolder, Item item);
}
