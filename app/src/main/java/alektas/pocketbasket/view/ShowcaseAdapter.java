package alektas.pocketbasket.view;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import alektas.pocketbasket.R;
import alektas.pocketbasket.db.entity.Item;
import alektas.pocketbasket.viewmodel.ItemsViewModel;

public class ShowcaseAdapter extends BaseItemAdapter {
    private static final String TAG = "ShowcaseAdapter";
    private MainActivity mActivity;
    private ItemsViewModel mModel;
    private List<Item> mDelItems;

    ShowcaseAdapter(MainActivity activity, ItemsViewModel model) {
        super(activity.getApplicationContext());
        mActivity = activity;
        mModel = model;
        mDelItems = model.getDelItems();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = super.getView(position, convertView, parent);
        ViewHolder viewHolder = (ViewHolder) itemView.getTag();
        viewHolder.mName.setTextColor(Color.WHITE);

        Item item = (Item) getItem(position);
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
        return itemView;
    }

    // show item name in showcase mode and hide in basket mode in "Showcase"
    @Override
    void setItemText(ViewHolder viewHolder, Item item) {
        if (mModel.isShowcaseNamesShow()) {
            viewHolder.mName.setText(getItemName(item));
        }
        else viewHolder.mName.setText("");
    }

    // add choose image to icon of item in showcase if item is present in basket
    @Override
    void setChooseIcon(ViewHolder viewHolder, Item item) {
        if (item.isInBasket()) {
            viewHolder.mCheckImage.setImageResource(R.drawable.ic_choosed);
        }
        else {
            viewHolder.mCheckImage.setImageResource(0);
        }
    }

    // add delete image to icon of item in showcase if item is choosed in Delete Mode
    private void setDelIcon(ViewHolder viewHolder, Item item) {
        if (mModel.isDelMode() && mDelItems.contains(item)) {
            viewHolder.mDelImage.setImageResource(R.drawable.ic_deleting);
        } else {
            viewHolder.mDelImage.setImageResource(0);
        }
    }

    public void deleteItems() {
        mModel.deleteAll(mDelItems);
        cancelDel();
    }

    public void cancelDel() {
        disableDelMode();
        mDelItems.clear();
        notifyDataSetChanged();
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
