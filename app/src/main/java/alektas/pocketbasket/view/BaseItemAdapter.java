package alektas.pocketbasket.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import alektas.pocketbasket.R;
import alektas.pocketbasket.db.entity.Item;

public abstract class BaseItemAdapter extends BaseAdapter {
    private Context mContext;
    private List<Item> mItems;

    BaseItemAdapter(Context context) {
        mContext = context;
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

    @SuppressLint("ClickableViewAccessibility")
    public View getView(int position, View convertView, final ViewGroup parent) {
        View itemView = convertView;

        ViewHolder viewHolder;
        if (itemView == null) {
            itemView = initView(parent);
            viewHolder = new ViewHolder(itemView);
            itemView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        bindViewWithData(viewHolder, mItems.get(position));

        return itemView;
    }

    public void setItems(List<Item> items) {
        mItems = items;
        notifyDataSetChanged();
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

    // hide item name in showcase mode and show in basket mode in "Basket"
    abstract void setItemText(ViewHolder viewHolder, Item item);

    // add check image to icon of item in basket if item is checked
    abstract void setChooseIcon(ViewHolder viewHolder, Item item);

    // get item name from resources or from key field if res is absent
    String getItemName(Item item) {
        if (item.getNameRes() == 0) return item.getName();
        try {
            return mContext.getString(item.getNameRes());
        }
        catch (Resources.NotFoundException e) {
            return item.getName();
        }
    }
}
