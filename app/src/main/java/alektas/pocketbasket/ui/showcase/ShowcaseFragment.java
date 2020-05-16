package alektas.pocketbasket.ui.showcase;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import alektas.pocketbasket.R;
import alektas.pocketbasket.ads.AdManager;
import alektas.pocketbasket.data.db.entities.ShowcaseItem;
import alektas.pocketbasket.ui.ComponentProvider;
import alektas.pocketbasket.ui.ItemSizeProvider;
import alektas.pocketbasket.utils.NetworkMonitor;

public class ShowcaseFragment extends Fragment {
    @Inject
    NetworkMonitor mNetworkMonitor;
    @Inject
    AdManager mAdManager;
    @Inject
    ShowcaseViewModel mViewModel;
    private ComponentProvider mComponentProvider;
    private ShowcaseRvAdapter mShowcaseAdapter;
    private ItemSizeProvider mItemSizeProvider;
    private RecyclerView mShowcase;
    private List<ShowcaseItem> mProducts = new ArrayList<>();

    public ShowcaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            mComponentProvider = (ComponentProvider) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement ComponentProvider");
        }

        try {
            mItemSizeProvider = (ItemSizeProvider) getContext();
        } catch (ClassCastException e) {
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
        mComponentProvider.getComponent().inject(this);

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
