package alektas.pocketbasket.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import alektas.pocketbasket.R;
import alektas.pocketbasket.db.entity.Item;
import alektas.pocketbasket.viewmodel.ItemsViewModel;

public class ShowcaseAdapter extends BaseAdapter {
    private static final String TAG = "ShowcaseAdapter";
    private MainActivity mActivity;
    private Context mContext;
    private ItemsViewModel mModel;
    private List<Item> mItems;
    private List<Item> mDelItems;

    ShowcaseAdapter(MainActivity activity, ItemsViewModel model) {
        mActivity = activity;
        mContext = activity.getApplicationContext();
        mModel = model;
        mDelItems = model.getDelItems();
    }

    static class ViewHolder {
        final View mItemView;
        final ImageView mImage;
        final TextView mIconText;
        final ImageView mCheckImage;
        final ImageView mDelImage;
        final TextView mName;

        ViewHolder(View view) {
            mItemView = view;
            mImage = mItemView.findViewById(R.id.item_image);
            mIconText = mItemView.findViewById(R.id.info_text);
            mCheckImage = mItemView.findViewById(R.id.check_image);
            mName = mItemView.findViewById(R.id.item_name);
            mDelImage = mItemView.findViewById(R.id.del_image);
        }
    }

    public int getCount() {
        if (mItems != null) return mItems.size();
        return 0;
    }

    public Object getItem(int position) {
        return mItems.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;

        ViewHolder viewHolder;
        if (itemView == null) {
            itemView = initView(parent);
            viewHolder = new ViewHolder(itemView);
            itemView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Item item;
        if (mItems != null) { item = mItems.get(position); }
        else item = new Item("Item");

        viewHolder.mName.setTextColor(Color.WHITE);

        bindViewWithData(viewHolder, item);

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

    public void setItems(List<Item> items) {
        mItems = items;
        notifyDataSetChanged();
        Log.d(TAG, "setItems: showcase is notified");
    }

    // inflate item View from resources
    // or get ViewHolder from saves if exist
    private View initView(ViewGroup parent) {
        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        return inflater.inflate(R.layout.item_view, parent, false);
    }

    private void bindViewWithData(ViewHolder viewHolder, Item item) {
        setItemText(viewHolder, item);
        setItemIcon(viewHolder, item);
        setChooseIcon(viewHolder, item);
        setDelIcon(viewHolder, item);
    }

    // show item name in showcase mode and hide in basket mode in "Showcase"
    private void setItemText(ViewHolder viewHolder, Item item) {
        if (mModel.isShowcaseNamesShow()) {
            viewHolder.mName.setText(getItemName(item));
        }
        else viewHolder.mName.setText("");
    }

    // set item icon (or name instead)
    private void setItemIcon(ViewHolder viewHolder, Item item) {
        if (item.getImgRes() > 0) {
            viewHolder.mImage.setImageResource(item.getImgRes());
            viewHolder.mIconText.setText("");
        } else {
            viewHolder.mImage.setImageResource(0);
            viewHolder.mIconText.setText(getItemName(item));
        }
    }

    // add choose image to icon of item in showcase if item is present in basket
    private void setChooseIcon(ViewHolder viewHolder, Item item) {
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

    // get item name from resources or from key field if res is absent
    private String getItemName(Item item) {
        String itemName;
        int nameRes = item.getNameRes();
        try {
            itemName = mContext.getString(nameRes);
        }
        catch (Resources.NotFoundException e) {
            itemName = item.getName();
        }
        return itemName;
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
