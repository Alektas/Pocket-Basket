package alektas.pocketbasket.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import alektas.pocketbasket.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SpinnerAdapter extends ArrayAdapter<Integer> {
    private Context mContext;
    private List<Integer> mTagsRes;

    public SpinnerAdapter(@NonNull Context context,
                          int resource,
                          @NonNull List<Integer> tagsRes) {
        super(context, resource, tagsRes);
        mContext = context;
        mTagsRes = tagsRes;
    }

    @Nullable
    @Override
    public Integer getItem(int position) {
        return mTagsRes.get(position);
    }

    @Override
    public int getCount() {
        if (mTagsRes != null) return mTagsRes.size();
        return 0;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View row = inflater.inflate(R.layout.category_view, parent, false);

        TextView label = (TextView) row.findViewById(R.id.category_btn_text);
        label.setText(mContext.getResources().getString(getItem(position)));

        ImageView icon = (ImageView) row.findViewById(R.id.category_btn_img);

        switch (getItem(position)) {
            case R.string.all: {
                icon.setImageResource(R.drawable.ic_done_all_white_24dp);
                break;
            }
            case R.string.drink: {
                icon.setImageResource(R.drawable.ic_rb_drinks);
                break;
            }
            case R.string.fruit: {
                icon.setImageResource(R.drawable.ic_rb_fruits);
                break;
            }
            case R.string.vegetable: {
                icon.setImageResource(R.drawable.ic_rb_vegs);
                break;
            }
            case R.string.floury: {
                icon.setImageResource(R.drawable.ic_rb_floury);
                break;
            }
            case R.string.milky: {
                icon.setImageResource(R.drawable.ic_rb_milky);
                break;
            }
            case R.string.groats: {
                icon.setImageResource(R.drawable.ic_rb_groats);
                break;
            }
            case R.string.sweets: {
                icon.setImageResource(R.drawable.ic_rb_sweets);
                break;
            }
            case R.string.meat: {
                icon.setImageResource(R.drawable.ic_rb_meat);
                break;
            }
            case R.string.seafood: {
                icon.setImageResource(R.drawable.ic_rb_fish);
                break;
            }
            case R.string.semis: {
                icon.setImageResource(R.drawable.ic_rb_semis);
                break;
            }
            case R.string.sauce_n_oil: {
                icon.setImageResource(R.drawable.ic_rb_oil);
                break;
            }
            case R.string.household: {
                icon.setImageResource(R.drawable.ic_rb_household);
                break;
            }
            case R.string.other: {
                icon.setImageResource(R.drawable.ic_more_horiz_white_24dp);
                break;
            }
        }

        return row;
    }
}
