package alektas.pocketbasket.ui.showcase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import alektas.pocketbasket.R;
import alektas.pocketbasket.databinding.ItemShowcaseBinding;
import alektas.pocketbasket.ui.utils.BaseRecyclerAdapter;
import alektas.pocketbasket.ui.ItemSizeProvider;

public class ShowcaseRvAdapter extends BaseRecyclerAdapter {
    private static final int PRODUCT_VIEW_TYPE = 0;

    private final ShowcaseViewModel mModel;
    private final ItemSizeProvider mSizeProvider;

    public ShowcaseRvAdapter(@NonNull ShowcaseViewModel model, ItemSizeProvider itemSizeProvider) {
        super();
        mModel = model;
        // Need to fix the item width
        // Width depends on the configuration (landscape or portrait)
        mSizeProvider = itemSizeProvider;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_showcase, parent, false);
        itemView.getLayoutParams().width = mSizeProvider.getItemWidth();
        itemView.requestLayout();
        ItemShowcaseBinding binding = DataBindingUtil.bind(itemView);
        binding.setModel(mModel);
        return new ItemHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
    }

    @Override
    public int getItemViewType(int position) {
        return PRODUCT_VIEW_TYPE;
    }

}
