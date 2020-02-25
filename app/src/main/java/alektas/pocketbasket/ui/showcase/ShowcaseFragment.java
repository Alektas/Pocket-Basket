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
import java.util.List;

import alektas.pocketbasket.R;
import alektas.pocketbasket.ads.AdManager;
import alektas.pocketbasket.domain.entities.ShowcaseItemModel;
import alektas.pocketbasket.ui.ItemSizeProvider;

public class ShowcaseFragment extends Fragment {
    private AdManager mAdManager;
    private List<ShowcaseItemModel> mProducts = new ArrayList<>();
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        mAdManager = new AdManager.Builder(requireContext(), R.string.ad_app_id, R.string.ad_unit_id)
                .withDebugAppId(R.string.ad_test_app_id)
                .withDebugAdId(R.string.ad_test_unit_id)
                .withTestDevice(R.string.ad_test_device_id)
                .build();

        mAdManager.fetchAds(true, new AdManager.AdsLoadingListener() {
            @Override
            public void onLoadFinished() {
                mShowcaseAdapter.setItems(mAdManager.combineWithLatestAds(mProducts));
            }

            @Override
            public void onLoadFailed() {  }
        });

        super.onActivityCreated(savedInstanceState);
    }

    private void subscribeOnModel() {
        mViewModel.getShowcaseData().observe(this, items -> {
            mProducts = items;
            mShowcaseAdapter.setItems(mAdManager.combineWithLatestAds(mProducts));
        });

        mViewModel.delModeState().observe(this, isDelMode -> {
            // Update item views to remove deletion selection border
            if (!isDelMode) mShowcaseAdapter.setItems(mShowcaseAdapter.getItems());
        });
    }

}
