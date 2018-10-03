package alektas.pocketbasket.view;

import android.content.Context;
import android.content.res.Resources;
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
    private List<Item> mItems;
    private Context mContext;
    private ItemsViewModel mModel;

    ShowcaseAdapter(Context context, ItemsViewModel model) {
        mContext = context;
        mModel = model;
    }

    static class ViewHolder {
        final View mItemView;
        final ImageView mImage;
        final TextView mText;
        final ImageView mCheckImage;
        final TextView mName;

        ViewHolder(View view) {
            mItemView = view;
            mImage = mItemView.findViewById(R.id.item_image);
            mText = mItemView.findViewById(R.id.info_text);
            mCheckImage = mItemView.findViewById(R.id.check_image);
            mName = mItemView.findViewById(R.id.item_name);
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

        bindViewWithData(viewHolder, item);

        viewHolder.mItemView.setOnClickListener(view -> {
            if (mModel.getBasketItem(item.getName()) == null) {
                mModel.putItem(item);
            }
            else {
                mModel.deleteItem(item.getName());
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
        // get item's icon res and name
        int imgRes = item.getImgRes();
        String itemName = getItemName(item);

        // show item name in showcase mode and hide in basket mode in "Showcase"
        if (mModel.isShowcaseNamesShow()) {
            viewHolder.mName.setText(itemName);
        }
        else viewHolder.mName.setText("");

        // set item icon (or name instead)
        if (imgRes > 0) {
            viewHolder.mImage.setImageResource(imgRes);
            viewHolder.mText.setText("");
        } else {
            viewHolder.mImage.setImageResource(0);
            viewHolder.mText.setText(itemName);
        }

        // add choose image to icon of item in showcase if item is present in basket
        if (item.isInBasket()) {
            viewHolder.mCheckImage.setImageResource(R.drawable.ic_choosed);
        }
        else {
            viewHolder.mCheckImage.setImageResource(0);
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
}
