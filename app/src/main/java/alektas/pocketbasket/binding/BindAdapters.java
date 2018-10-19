package alektas.pocketbasket.binding;

import android.widget.ImageView;
import android.widget.TextView;

import alektas.pocketbasket.db.entity.Item;
import androidx.databinding.BindingAdapter;

public class BindAdapters {
    @BindingAdapter("android:src")
    public static void setImage(ImageView view, int imgRes) {
        view.setImageResource(imgRes);
    }

    @BindingAdapter("android:text")
    public static void setText(TextView view, Item item) {
        if (item == null) view.setText("");
        else {
            if (item.getNameRes() == 0) view.setText(item.getName());
            else view.setText(view.getContext().getString(item.getNameRes()));
        }
    }
}
