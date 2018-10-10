package alektas.pocketbasket.view;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import alektas.pocketbasket.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AddItemFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_item_layout, container, false);
    }
}
