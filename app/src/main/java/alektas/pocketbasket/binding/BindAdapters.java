package alektas.pocketbasket.binding;

import android.content.res.Resources;
import android.view.View;
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

    @BindingAdapter("android:onTouch")
    public static void setTouchListener(View self, View.OnTouchListener listener) {
        self.setOnTouchListener(listener);
    }

}
