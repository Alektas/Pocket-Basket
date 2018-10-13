package alektas.pocketbasket.view;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.ViewGroup;

import java.util.List;

import alektas.pocketbasket.R;
import alektas.pocketbasket.db.entity.Item;
import alektas.pocketbasket.viewmodel.ItemsViewModel;
import androidx.annotation.NonNull;

public class ShowcaseRvAdapter extends BaseRecyclerAdapter {
    private static final String TAG = "ShowcaseAdapter";
    private MainActivity mActivity;
    private ItemsViewModel mModel;
    private List<Item> mDelItems;

    ShowcaseRvAdapter(MainActivity activity, ItemsViewModel model) {
        super(activity.getApplicationContext());
        mActivity = activity;
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

        Item item = getItems().get(position);
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

    // show item name in showcase mode and hide in basket mode in "Showcase"
    @Override
    public void setItemText(ViewHolder viewHolder, Item item) {
        if (mModel.isShowcaseNamesShow()) {
            viewHolder.mName.setText(getItemName(item));
        }
        else viewHolder.mName.setText("");
    }

    @Override
    public void setChooseIcon(ViewHolder viewHolder, Item item) {
        if(item.isInBasket()) {
            viewHolder.mCheckImage.setImageResource(R.drawable.ic_choosed);
        }
        else {
            viewHolder.mCheckImage.setImageResource(0);
        }
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

    // add delete image to icon of item in showcase if item is choosed in Delete Mode
    private void setDelIcon(ViewHolder viewHolder, Item item) {
        if (mModel.isDelMode() && mDelItems.contains(item)) {
            viewHolder.mDelImage.setImageResource(R.drawable.ic_deleting);
        } else {
            viewHolder.mDelImage.setImageResource(0);
        }
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
