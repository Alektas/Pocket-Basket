package alektas.pocketbasket.view;

import android.content.Context;
import android.content.res.Resources;
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

public class ShowcaseAdapter extends BaseAdapter {
    private List<Data> mItems;
    private IPresenter mPresenter;
    private Context mContext;

    ShowcaseAdapter(Context context, IPresenter presenter) {
        mContext = context;
        mPresenter = presenter;
        mItems = presenter.getShowcaseItems();
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
        return mItems.size();
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

        final Data item = mItems.get(position);
        bindViewWithData(viewHolder, item);

        viewHolder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPresenter.getData(item.getKey()) == null) {
                    mPresenter.addData(item.getKey());
                }
                else {
                    mPresenter.deleteData(item.getKey());
                }
            }
        });
        return itemView;
    }

    // inflate item View from resources
    // or get ViewHolder from saves if exist
    private View initView(ViewGroup parent) {
        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        return inflater.inflate(R.layout.item_view, parent, false);
    }

    private void bindViewWithData(ViewHolder viewHolder, Data item) {
        // get item's icon res and name
        int imgRes = item.getImgRes();
        String itemName = getItemName(item);

        // show item name in showcase mode and hide in basket mode in "Showcase"
        if (mPresenter.isShowcaseMode()) {
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
        if (mPresenter.inBasket(item.getKey())) {
            viewHolder.mCheckImage.setImageResource(R.drawable.ic_choosed);
        }
        else {
            viewHolder.mCheckImage.setImageResource(0);
        }
    }

    // get item name from resources or from key field if res is absent
    private String getItemName(Data item) {
        String itemName;
        int nameRes = item.getNameRes();
        try {
            itemName = mContext.getString(nameRes);
        }
        catch (Resources.NotFoundException e) {
            itemName = item.getKey();
        }
        return itemName;
    }
}
