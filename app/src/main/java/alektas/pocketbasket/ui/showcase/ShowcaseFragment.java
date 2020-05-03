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
import java.util.List;

import javax.inject.Inject;

import alektas.pocketbasket.App;
import alektas.pocketbasket.R;
import alektas.pocketbasket.ads.AdManager;
import alektas.pocketbasket.data.db.entities.ShowcaseItem;
import alektas.pocketbasket.ui.ItemSizeProvider;
import alektas.pocketbasket.utils.NetworkMonitor;

public class ShowcaseFragment extends Fragment {
    @Inject
    NetworkMonitor mNetworkMonitor;
    @Inject
    AdManager mAdManager;
    @Inject
    ViewModelProvider.Factory mViewModelFactory;
    private ShowcaseViewModel mViewModel;
    private ShowcaseRvAdapter mShowcaseAdapter;
    private ItemSizeProvider mItemSizeProvider;
    private RecyclerView mShowcase;
    private List<ShowcaseItem> mProducts = new ArrayList<>();

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
        App.getComponent().inject(this);
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
        mViewModel = new ViewModelProvider(this, mViewModelFactory).get(ShowcaseViewModel.class);
        mShowcaseAdapter = new ShowcaseRvAdapter(mViewModel, mItemSizeProvider);
        mShowcase.setAdapter(mShowcaseAdapter);
        subscribeOnModel();

        mNetworkMonitor.setNetworkListener(isAvailable -> {
            if (!isAvailable) return;

            mAdManager.fetchAds(new AdManager.AdsLoadingListener() {
                @Override
                public void onLoadFinished() {
                    mShowcaseAdapter.setItems(mAdManager.combineWithLatestAds(mProducts));
                }

                @Override
                public void onLoadFailed(int errorCode) {  }
            });
        });

        super.onActivityCreated(savedInstanceState);
    }

    private void subscribeOnModel() {
        mViewModel.getShowcaseData().observe(getViewLifecycleOwner(), items -> {
            mProducts = items;
            mShowcaseAdapter.setItems(mAdManager.combineWithLatestAds(mProducts));
        });
    }

    @Override
    public void onDestroyView() {
        mNetworkMonitor.removeNetworkListener();
        super.onDestroyView();
    }
}
