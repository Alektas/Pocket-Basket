package alektas.pocketbasket.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import alektas.pocketbasket.R;
import alektas.pocketbasket.db.entity.Item;
import alektas.pocketbasket.viewmodel.ItemsViewModel;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ShowcaseRvAdapter extends RecyclerView.Adapter<ShowcaseRvAdapter.ShowcaseViewHolder> {
    private static final String TAG = "ShowcaseAdapter";
    private MainActivity mActivity;
    private Context mContext;
    private ItemsViewModel mModel;
    private List<Item> mItems;
    private List<Item> mDelItems;

    ShowcaseRvAdapter(MainActivity activity, ItemsViewModel model) {
        mActivity = activity;
        mContext = activity.getApplicationContext();
        mModel = model;
        mDelItems = model.getDelItems();
    }

    static class ShowcaseViewHolder extends RecyclerView.ViewHolder {
        final View mItemView;
        final ImageView mImage;
        final TextView mIconText;
        final ImageView mCheckImage;
        final ImageView mDelImage;
        final TextView mName;

        ShowcaseViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemView = itemView;
            mImage = mItemView.findViewById(R.id.item_image);
            mIconText = mItemView.findViewById(R.id.info_text);
            mCheckImage = mItemView.findViewById(R.id.check_image);
            mName = mItemView.findViewById(R.id.item_name);
            mDelImage = mItemView.findViewById(R.id.del_image);
        }
    }

    @Override
    public int getItemCount() {
        if (mItems != null) return mItems.size();
        return 0;
    }

    @NonNull
    @Override
    public ShowcaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_view, parent, false);

        ShowcaseViewHolder viewHolder = new ShowcaseViewHolder(view);
        viewHolder.mName.setTextColor(Color.WHITE);
        return viewHolder;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ShowcaseViewHolder viewHolder, int position) {
        Item item = mItems.get(position);
        setItemText(viewHolder, item);
        setItemIcon(viewHolder, item);
        setChooseIcon(viewHolder, item);
        setDelIcon(viewHolder, item);

        viewHolder.mItemView.setOnLongClickListener(view -> {
            enableDelMode();
            prepareToDel(item);
            return true;
        });

        viewHolder.mItemView.setOnClickListener(view -> {
            if (mModel.isDelMode()) {
                if (mDelItems.contains(item)) { removeFromDel(item); }
                else { prepareToDel(item); }
            } else {
                if (mModel.getBasketItem(item.getName()) == null) {
                    mModel.putItem(item);
                }
                else {
                    mModel.removeBasketItem(item.getName());
                }
            }
        });
    }

    public void setItems(List<Item> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    public void deleteChoosedItems() {
        mModel.deleteAll(mDelItems);
        cancelDel();
    }

    public void cancelDel() {
        disableDelMode();
        mDelItems.clear();
        notifyDataSetChanged();
    }

    // show item name in showcase mode and hide in basket mode in "Showcase"
    private void setItemText(ShowcaseViewHolder viewHolder, Item item) {
        if (mModel.isShowcaseNamesShow()) {
            viewHolder.mName.setText(getItemName(item));
        }
        else viewHolder.mName.setText("");
    }

    // set item icon (or name instead)
    private void setItemIcon(ShowcaseViewHolder viewHolder, Item item) {
        if (item.getImgRes() > 0) {
            viewHolder.mImage.setImageResource(item.getImgRes());
            viewHolder.mIconText.setText("");
        } else {
            viewHolder.mImage.setImageResource(0);
            viewHolder.mIconText.setText(getItemName(item));
        }
    }

    // add choose image to icon of item in showcase if item is present in basket
    private void setChooseIcon(ShowcaseViewHolder viewHolder, Item item) {
        if (item.isInBasket()) {
            viewHolder.mCheckImage.setImageResource(R.drawable.ic_choosed);
        }
        else {
            viewHolder.mCheckImage.setImageResource(0);
        }
    }

    // add delete image to icon of item in showcase if item is choosed in Delete Mode
    private void setDelIcon(ShowcaseViewHolder viewHolder, Item item) {
        if (mModel.isDelMode() && mDelItems.contains(item)) {
            viewHolder.mDelImage.setImageResource(R.drawable.ic_deleting);
        } else {
            viewHolder.mDelImage.setImageResource(0);
        }
    }

    // get item name from resources or from key field if res is absent
    private String getItemName(Item item) {
        int nameRes = item.getNameRes();
        if (nameRes == 0) { return item.getName(); }
        try {
            return mContext.getString(nameRes);
        }
        catch (Resources.NotFoundException e) { return item.getName(); }
    }

    private void prepareToDel(Item item) {
        mDelItems.add(item);
        notifyDataSetChanged();
    }

    private void removeFromDel(Item item) {
        mDelItems.remove(item);
        notifyDataSetChanged();
    }

    private void enableDelMode() {
        mModel.setDelMode(true);
        mActivity.onDelModeEnable();
    }

    private void disableDelMode() {
        mModel.setDelMode(false);
        mActivity.onDelModeDisable();
    }
}
