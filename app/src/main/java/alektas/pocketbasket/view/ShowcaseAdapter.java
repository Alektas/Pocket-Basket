package alektas.pocketbasket.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import alektas.pocketbasket.IPresenter;
import alektas.pocketbasket.model.Data;

public class ShowcaseAdapter extends ItemListAdapter {
    private List<Data> mData;
    private IPresenter mPresenter;

    public ShowcaseAdapter(Context context, IPresenter presenter) {
        super(context, presenter, presenter.getAll(), true);
        mPresenter = presenter;
        mData = presenter.getAll();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = super.getView(position, convertView, parent);
        final Data item = mData.get(position);
        ViewHolder viewHolder = super.getViewHolder();
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
