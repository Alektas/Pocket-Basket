package alektas.pocketbasket.ui.showcase;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import alektas.pocketbasket.R;
import alektas.pocketbasket.ui.ItemSizeProvider;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowcaseFragment extends Fragment {
    private static final String TAG = "ShowcaseFragment";
    private ShowcaseViewModel mViewModel;
    private ShowcaseRvAdapter mShowcaseAdapter;
    private ItemSizeProvider mItemSizeProvider;

    public ShowcaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the interface so we can get data from the host
            mItemSizeProvider = (ItemSizeProvider) getContext();
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getContext()
                    + " must implement ItemSizeProvider");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(ShowcaseViewModel.class);
        mShowcaseAdapter = new ShowcaseRvAdapter(mViewModel, mItemSizeProvider);
        subscribeOnModel();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_showcase, container, false);

        RecyclerView showcase = root.findViewById(R.id.showcase_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        showcase.setLayoutManager(layoutManager);
        showcase.setHasFixedSize(true);
        showcase.setAdapter(mShowcaseAdapter);

        return root;
    }

    private void subscribeOnModel() {
        mViewModel.getShowcaseData().observe(this, items -> {
            mShowcaseAdapter.setItems(new ArrayList<>(items));
        });

        mViewModel.delModeState().observe(this, isDelMode -> {
            if (!isDelMode) mShowcaseAdapter.notifyDataSetChanged();
        });
    }

}
