package alektas.pocketbasket.view;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import alektas.pocketbasket.R;
import alektas.pocketbasket.db.entity.Item;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseRecyclerAdapter extends RecyclerView.Adapter<BaseRecyclerAdapter.ViewHolder> {
    private Context mContext;
    private List<Item> mItems;

    BaseRecyclerAdapter(Context context) {
        mContext = context;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final View mItemView;
        final ImageView mImage;
        final TextView mIconText;
        final ImageView mCheckImage;
        final ImageView mDelImage;
        final TextView mName;

        ViewHolder(@NonNull View itemView) {
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Item item = mItems.get(position);
        setItemText(viewHolder, item);
        setItemIcon(viewHolder, item);
        setChooseIcon(viewHolder, item);
    }

    public List<Item> getItems() {
        return mItems;
    }

    public void setItems(List<Item> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    public abstract void setItemText(ViewHolder viewHolder, Item item);

    // add check image to icon of item in basket if item is checked
    public void setChooseIcon(ViewHolder viewHolder, Item item) {
        if (item.isChecked()) {
            viewHolder.mCheckImage.setImageResource(R.drawable.ic_checked);
        }
        else {
            viewHolder.mCheckImage.setImageResource(0);
        }
    }

    // set item icon (or name instead)
    private void setItemIcon(ViewHolder viewHolder, Item item) {
        if (item.getImgRes() != 0) {
            viewHolder.mImage.setImageResource(item.getImgRes());
            viewHolder.mIconText.setText("");
        } else {
            viewHolder.mImage.setImageResource(0);
            viewHolder.mIconText.setText(getItemName(item));
        }
    }

    // get item name from resources or from key field if res is absent
    String getItemName(Item item) {
        int nameRes = item.getNameRes();
        if (nameRes == 0) { return item.getName(); }
        try {
            return mContext.getString(nameRes);
        }
        catch (Resources.NotFoundException e) { return item.getName(); }
    }
}
