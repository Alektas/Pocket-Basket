package alektas.pocketbasket.binding;

import android.content.res.Resources;
import android.widget.ImageView;
import android.widget.TextView;

import alektas.pocketbasket.db.entity.Item;
import androidx.databinding.BindingAdapter;

public class BindAdapters {
    @BindingAdapter("android:src")
    public static void setImage(ImageView view, int imgRes) {
        try {
            view.setImageResource(imgRes);
        }
        catch (Resources.NotFoundException e) {
            view.setImageResource(0);
        }
    }

    @BindingAdapter("android:text")
    public static void setText(TextView view, Item item) {
        if (item == null) view.setText("");
        else {
            try {
                view.setText(view.getContext().getString(item.getNameRes()));
            }
            catch (Resources.NotFoundException e) {
                view.setText(item.getName());
            }
        }
    }
}
