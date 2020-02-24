package alektas.pocketbasket.ui.showcase;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.formats.NativeAdOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import alektas.pocketbasket.BuildConfig;
import alektas.pocketbasket.R;
import alektas.pocketbasket.domain.entities.ShowcaseItemModel;
import alektas.pocketbasket.ui.ItemSizeProvider;
import alektas.pocketbasket.ui.UiContract;
import alektas.pocketbasket.ui.utils.NativeAdWrapper;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowcaseFragment extends Fragment {
    // The number of native ads to load and display.
    private static final int NUMBER_OF_ADS = 5;
    private static final int MIN_OFFSET_OF_ADS = 5;
    // The AdLoader used to load ads.
    private AdLoader mAdLoader;
    // List of native ads that have been successfully loaded.
    private List<NativeAdWrapper> mNativeAds = new ArrayList<>();
    private List<ShowcaseItemModel> mProducts = new ArrayList<>();
    private ShowcaseViewModel mViewModel;
    private ShowcaseRvAdapter mShowcaseAdapter;
    private ItemSizeProvider mItemSizeProvider;
    private boolean isShowcaseMode = UiContract.IS_DEFAULT_MODE_SHOWCASE;

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
        initAds(requireContext(), mShowcaseAdapter);
        super.onActivityCreated(savedInstanceState);
    }

    private void initAds(Context context, ShowcaseRvAdapter adapter) {
        String appId = BuildConfig.DEBUG ? getString(R.string.ad_test_app_id) : getString(R.string.ad_app_id);
        MobileAds.initialize(context, appId);

        List<String> testDeviceIds = Collections.singletonList(getString(R.string.ad_test_device_id));
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);

        String adUnitId = BuildConfig.DEBUG ? getString(R.string.ad_test_unit_id) : getString(R.string.ad_unit_id);
        mAdLoader = new AdLoader.Builder(context, adUnitId)
                .forUnifiedNativeAd(unifiedNativeAd -> {
                    mNativeAds.add(new NativeAdWrapper(unifiedNativeAd, isShowcaseMode));
                    if (!mAdLoader.isLoading()) {
                        adapter.setItems(combine(mProducts, mNativeAds));
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        Log.d("Loading ads", "Failed to load with code: " + errorCode);
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();

        mAdLoader.loadAds(new AdRequest.Builder().build(), NUMBER_OF_ADS);
    }

    private void subscribeOnModel() {
        mViewModel.getShowcaseData().observe(this, items -> {
            mProducts = items;
            mShowcaseAdapter.setItems(combine(mProducts, mNativeAds));
        });

        mViewModel.delModeState().observe(this, isDelMode -> {
            if (!isDelMode) mShowcaseAdapter.setItems(mShowcaseAdapter.getItems());
        });

        mViewModel.getShowcaseModeData().observe(this, isShowcaseMode -> {
            this.isShowcaseMode = isShowcaseMode;
            mShowcaseAdapter.setMode(isShowcaseMode);
            mNativeAds = withChangedIconState(mNativeAds, isShowcaseMode);
            mShowcaseAdapter.setItems(combine(mProducts, mNativeAds));
        });
    }

    private List<Object> combine(List<ShowcaseItemModel> products, List<NativeAdWrapper> ads) {
        if (ads.size() <= 0) return new ArrayList<>(products);

        int totalSize = products.size() + ads.size();
        List<Object> items = new ArrayList<>(totalSize);

        int offset = Math.max((items.size() / ads.size() + 1), MIN_OFFSET_OF_ADS);
        int adPointer = 0;
        int productPointer = 0;
        int adIndex = 0;
        for (int i = 0; i < totalSize; i++) {
            if (i == adIndex && adPointer < ads.size()) {
                items.add(ads.get(adPointer));
                adPointer++;
                adIndex += offset;
            } else if (productPointer < products.size()){
                items.add(products.get(productPointer));
                productPointer++;
            }
        }

        return items;
    }

    private List<NativeAdWrapper> withChangedIconState(List<NativeAdWrapper> ads, boolean isIconsShown) {
        List<NativeAdWrapper> newAds = new ArrayList<>(ads.size());
        for (NativeAdWrapper w : ads) {
            newAds.add(new NativeAdWrapper(w.getAd(), isIconsShown));
        }
        return newAds;
    }

}
