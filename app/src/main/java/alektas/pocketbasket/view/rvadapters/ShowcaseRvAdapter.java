package alektas.pocketbasket.view.rvadapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import alektas.pocketbasket.R;
import alektas.pocketbasket.databinding.ItemShowcaseBinding;
import alektas.pocketbasket.view.ItemSizeProvider;
import alektas.pocketbasket.viewmodel.ItemsViewModel;

public class ShowcaseRvAdapter extends BaseRecyclerAdapter {
    private static final String TAG = "ShowcaseAdapter";

    private ItemsViewModel mModel;
    private int mItemWidth;

    public ShowcaseRvAdapter(ItemSizeProvider itemSizeProvider,
                             @NonNull ItemsViewModel model) {
        super();
        mModel = model;
        // Fix item width
        // Width depends on configuration (landscape or portrait)
        mItemWidth = itemSizeProvider.getItemWidth();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_showcase, parent, false);
        itemView.getLayoutParams().width = mItemWidth;
        itemView.requestLayout();
        ItemShowcaseBinding binding = DataBindingUtil.bind(itemView);
        binding.setModel(mModel);
        return new ItemHolder(binding);
    }

    public void setItems(List<Object> items) {
        super.setItems(items);
    }

}
