package alektas.pocketbasket.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import alektas.pocketbasket.R;
import alektas.pocketbasket.db.entity.Item;
import alektas.pocketbasket.viewmodel.ItemsViewModel;
import androidx.annotation.NonNull;

public class ShowcaseRvAdapter extends BaseRecyclerAdapter {
    private static final String TAG = "ShowcaseAdapter";
    private DeleteModeListener mDMListener;
    private ItemsViewModel mModel;
    private List<Item> mDelItems;

    ShowcaseRvAdapter(Context context,
                      DeleteModeListener delModeListener,
                      @NonNull ItemsViewModel model) {
        super(context, model);
        mDMListener = delModeListener;
        mModel = model;
        mDelItems = model.getDelItems();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
        viewHolder.mName.setTextColor(Color.WHITE);
        return viewHolder;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);

        setDelIcon(viewHolder, getItems().get(position));
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder viewHolder) {
        super.onViewAttachedToWindow(viewHolder);

        if (viewHolder != null && viewHolder.getAdapterPosition() != -1) {
            final Item item = getItems().get(viewHolder.getAdapterPosition());

            viewHolder.mItemView.setOnLongClickListener(view -> {
                if (!mModel.isDelMode()) {
                    enableDelMode();
                }
                prepareToDel(item, viewHolder);
                return true;
            });

            viewHolder.mItemView.setOnClickListener(view -> {
                if (mModel.isDelMode()) {
                    if (mDelItems.contains(item)) { removeFromDel(item, viewHolder); }
                    else { prepareToDel(item, viewHolder); }
                } else {
                    if (mModel.getBasketMeta(item.getName()) == null) {
                        mModel.putToBasket(item.getName());
                        notifyItemChanged(viewHolder.getAdapterPosition());
                    }
                    else {
                        mModel.removeFromBasket(item.getName());
                        notifyItemChanged(viewHolder.getAdapterPosition());
                    }
                }
            });
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder viewHolder) {
        super.onViewDetachedFromWindow(viewHolder);

        viewHolder.mItemView.setOnLongClickListener(null);
        viewHolder.mItemView.setOnClickListener(null);
    }

    // show item name in showcase mode and hide in basket mode in "Showcase"
    @Override
    void setItemText(ViewHolder viewHolder, Item item) {
        super.setItemText(viewHolder, item);
        if (mModel.isShowcaseNamesShow()) {
            viewHolder.mName.setVisibility(View.VISIBLE);
        }
        else viewHolder.mName.setVisibility(View.GONE);
    }

    @Override
    void setChooseIcon(ViewHolder viewHolder, Item item) {
        if(mModel.isInBasket(item)) {
            viewHolder.mCheckImage.setImageResource(R.drawable.ic_choosed);
        }
        else {
            viewHolder.mCheckImage.setImageResource(0);
        }
    }

    public void deleteChoosedItems() {
        mModel.deleteItems(new ArrayList<>(mDelItems));
        cancelDel();
    }

    public void cancelDel() {
        disableDelMode();
        mDelItems.clear();
        notifyDataSetChanged();
    }

    // add delete image to icon of item in showcase if item is choosed in Delete Mode
    private void setDelIcon(ViewHolder viewHolder, Item item) {
        if (mModel.isDelMode() && mDelItems.contains(item)) {
            viewHolder.mDelImage.setImageResource(R.drawable.ic_deleting);
        } else {
            viewHolder.mDelImage.setImageResource(0);
        }
    }

    private void prepareToDel(Item item, ViewHolder holder) {
        mDelItems.add(item);
        notifyItemChanged(holder.getAdapterPosition());
    }

    private void removeFromDel(Item item, ViewHolder holder) {
        mDelItems.remove(item);
        notifyItemChanged(holder.getAdapterPosition());
    }

    private void enableDelMode() {
        mModel.setDelMode(true);
        mDMListener.onDelModeEnable();
    }

    private void disableDelMode() {
        mModel.setDelMode(false);
        mDMListener.onDelModeDisable();
    }
}
