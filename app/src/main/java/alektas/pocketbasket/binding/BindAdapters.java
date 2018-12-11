package alektas.pocketbasket.binding;

import android.content.res.Resources;
import android.util.Log;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

public class BindAdapters {
    private static final String TAG = "BindAdapters";

    @BindingAdapter("android:src")
    public static void setImage(ImageView view, int imgRes) {
        try {
            view.setImageResource(imgRes);
        }
        catch (Resources.NotFoundException e) {
            view.setImageResource(0);
        }
    }
}
