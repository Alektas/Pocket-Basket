package alektas.pocketbasket.view;

import android.annotation.SuppressLint;
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

import alektas.pocketbasket.IPresenter;
import alektas.pocketbasket.R;
import alektas.pocketbasket.model.Data;

public class ItemListAdapter extends BaseAdapter {
    private static final String TAG = "ItemListAdapter";
    private Context mContext;
    private IPresenter mPresenter;
    private List<Data> mData;
    private ViewHolder mViewHolder;
    private boolean isShowcase;

    public ItemListAdapter(
            Context context, IPresenter presenter, List<Data> data, boolean isShowcase) {
        mContext = context;
        mPresenter = presenter;
        mData = data;
        this.isShowcase = isShowcase;
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
        return mData.size();
    }

    public Object getItem(int position) {
        return mData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    @SuppressLint("ClickableViewAccessibility")
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;

        // inflate View from resources or get Holder from saved if exist
        if (itemView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            itemView = inflater.inflate(R.layout.item_view, parent, false);
            mViewHolder = new ViewHolder(itemView);
            itemView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        // get item and it's icon res
        // get item name from resources or from key field if res is absent
        final Data item = mData.get(position);
        int imgRes = item.getImgRes();
        int nameRes = item.getNameRes();
        String itemName;
        try {
            itemName = mContext.getString(nameRes);
        }
        catch (Resources.NotFoundException e) {
            itemName = item.getKey();
        }

        // show item name in showcase mode and hide in basket mode in "Showcase"
        // and vice versa in "Basket"
        if (mPresenter.isShowcaseMode() && isShowcase
                || !mPresenter.isShowcaseMode() && !isShowcase) {
            mViewHolder.mName.setText(itemName);
        }
        else mViewHolder.mName.setText("");

        // set item icon (or name instead)
        if (imgRes > 0) {
            mViewHolder.mImage.setImageResource(imgRes);
            mViewHolder.mText.setText("");
        } else {
            mViewHolder.mImage.setImageResource(0);
            mViewHolder.mText.setText(itemName);
        }

        // add choose image to icon of item in showcase if item is present in basket
        if (mPresenter.inBasket(item.getKey()) && isShowcase) {
            mViewHolder.mCheckImage.setImageResource(R.drawable.ic_choosed);
        }
        // add check image to icon of item in basket if item is checked
        else if (item.isChecked() && !isShowcase) {
            mViewHolder.mCheckImage.setImageResource(R.drawable.ic_checked);
        }
        else {
            mViewHolder.mCheckImage.setImageResource(0);
        }

        return itemView;
    }

    ViewHolder getViewHolder() {
        return mViewHolder;
    }
}
