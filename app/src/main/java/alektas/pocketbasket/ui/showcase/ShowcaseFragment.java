package alektas.pocketbasket.ui.showcase;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import alektas.pocketbasket.R;
import alektas.pocketbasket.domain.entities.ShowcaseItemModel;
import alektas.pocketbasket.ui.ItemSizeProvider;

public class ShowcaseFragment extends Fragment {
    private List<ShowcaseItemModel> mProducts = new ArrayList<>();
    private ShowcaseViewModel mViewModel;
    private ShowcaseRvAdapter mShowcaseAdapter;
    private ItemSizeProvider mItemSizeProvider;
    private RecyclerView mShowcase;

    public ShowcaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_showcase, container, false);

        mShowcase = root.findViewById(R.id.showcase_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        mShowcase.setLayoutManager(layoutManager);
        mShowcase.setHasFixedSize(true);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(ShowcaseViewModel.class);
        mShowcaseAdapter = new ShowcaseRvAdapter(mViewModel, mItemSizeProvider);
        mShowcase.setAdapter(mShowcaseAdapter);
        subscribeOnModel();
        super.onActivityCreated(savedInstanceState);
    }

    private void subscribeOnModel() {
        mViewModel.getShowcaseData().observe(getViewLifecycleOwner(), items -> {
            mProducts = items;
            mShowcaseAdapter.setItems(Arrays.asList(mProducts.toArray()));
        });
    }
}
