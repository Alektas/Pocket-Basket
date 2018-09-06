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
    private List<Data> mData;
    private IPresenter mPresenter;
    private Context mContext;

    public ShowcaseAdapter(Context context, IPresenter presenter) {
        mContext = context;
        mPresenter = presenter;
        mData = presenter.getAll();
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;

        // inflate View from resources or get Holder from saved if exist
        ViewHolder viewHolder;
        if (itemView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            itemView = inflater.inflate(R.layout.item_view, parent, false);

            viewHolder = new ViewHolder(itemView);
            itemView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // get item and it's icon res
        final Data item = mData.get(position);
        int imgRes = item.getImgRes();

        // get item name from resources or from key field if res is absent
        int nameRes = item.getNameRes();
        String itemName;
        try {
            itemName = mContext.getString(nameRes);
        }
        catch (Resources.NotFoundException e) {
            itemName = item.getKey();
        }

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
}
