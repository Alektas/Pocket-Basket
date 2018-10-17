package alektas.pocketbasket.view;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import alektas.pocketbasket.R;
import alektas.pocketbasket.databinding.ItemViewBinding;
import alektas.pocketbasket.db.entity.Item;
import alektas.pocketbasket.viewmodel.ItemsViewModel;
import androidx.annotation.NonNull;
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
        final View mItemView;
        final ImageView mCheckImage;
        final ImageView mDelImage;
        final TextView mName;

        private ItemViewBinding mItemBinding;

        ViewHolder(@NonNull ItemViewBinding binding) {
            super(binding.getRoot());
            mItemBinding = binding;

            mItemView = itemView;
            mCheckImage = mItemView.findViewById(R.id.check_image);
            mName = mItemView.findViewById(R.id.item_name);
            mDelImage = mItemView.findViewById(R.id.del_image);
        }

        void bind(Item item) {
            mItemBinding.setItem(item);
            mItemBinding.executePendingBindings();
        }
    }

    @Override
    public int getItemCount() {
        if (mItems != null) return mItems.size();
        return 0;
    }

    @Override
    public long getItemId(int position) {
        int nameRes = mItems.get(position).getNameRes();
        if (nameRes != 0) return nameRes;
        else return mItems.get(position).getName().hashCode();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ItemViewBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.item_view, parent, false);
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

    public abstract void setItemText(ViewHolder viewHolder, Item item);

    public abstract void setChooseIcon(ViewHolder viewHolder, Item item);

    // get item name from resources or from key field if res is absent
    String getItemName(Item item) {
        int nameRes = item.getNameRes();
        if (nameRes == 0) { return item.getName(); }
        try {
            return mContext.getString(nameRes);
        }
        catch (Resources.NotFoundException e) { return item.getName(); }
    }
}
