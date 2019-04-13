package alektas.pocketbasket.view.rvadapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import alektas.pocketbasket.R;
import alektas.pocketbasket.databinding.ShowcaseItemViewBinding;
import alektas.pocketbasket.view.ItemSizeProvider;
import alektas.pocketbasket.viewmodel.ItemsViewModel;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

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
                .inflate(R.layout.showcase_item_view, parent, false);
        itemView.getLayoutParams().width = mItemWidth;
        itemView.requestLayout();
        ShowcaseItemViewBinding binding = DataBindingUtil.bind(itemView);
        binding.setModel(mModel);
        return new ItemHolder(binding);
    }

    public void setItems(List<Object> items) {
        super.setItems(items);
    }

}
