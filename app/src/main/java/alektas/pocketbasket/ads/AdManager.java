package alektas.pocketbasket.ads;

import android.content.Context;
import android.util.Log;

import androidx.annotation.StringRes;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.formats.NativeAdOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alektas.pocketbasket.BuildConfig;
import alektas.pocketbasket.domain.entities.ShowcaseItemModel;

public class AdManager {
    private static final int MIN_OFFSET_OF_ADS = 10;
    private static final int NUMBER_OF_ADS = 5;
    private List<NativeAdWrapper> mNativeAds = new ArrayList<>();
    private List<Integer> mLatestAdPositions = new ArrayList<>();
    private Context mContext;
    private String mAdId;
    private AdLoader mAdLoader;

    public interface AdsLoadingListener {
        void onLoadFinished();
        void onLoadFailed(int errorCode);
    }

    private AdManager(Context context, String appId, String adId, RequestConfiguration config) {
        MobileAds.initialize(context, appId);
        MobileAds.setRequestConfiguration(config);
        mContext = context;
        mAdId = adId;
    }

    public static class Builder {
        private Context mContext;
        private List<String> testDevices = new ArrayList<>();
        private String mAppId;
        private String mDebugAppId;
        private String mAdId;
        private String mDebugAdId;

        public Builder(Context context, @StringRes int appId, @StringRes int adId) {
            mContext = context;
            mAppId = context.getString(appId);
            mAdId = context.getString(adId);
        }

        public Builder withDebugAppId(@StringRes int appId) {
            mDebugAppId = mContext.getString(appId);
            return this;
        }

        public Builder withDebugAdId(@StringRes int adId) {
            mDebugAdId = mContext.getString(adId);
            return this;
        }

        public Builder withTestDevice(@StringRes int deviceId) {
            testDevices.add(mContext.getString(deviceId));
            return this;
        }

        public AdManager build() {
            String appId = BuildConfig.DEBUG ? mDebugAppId : mAppId;
            String adId = BuildConfig.DEBUG ? mDebugAdId : mAdId;
            RequestConfiguration config =
                    new RequestConfiguration.Builder().setTestDeviceIds(testDevices).build();
            return new AdManager(mContext, appId, adId, config);
        }
    }

    /**
     * Receives a new portion of advertising from the network. The operation is asynchronous, so the
     * listener must be provided to notify of the completion / failure of the download via callbacks.
     * @param listener gets loading callbacks
     */
    public void fetchAds(AdsLoadingListener listener) {
        mNativeAds.clear();

        mAdLoader = new AdLoader.Builder(mContext, mAdId)
                .forUnifiedNativeAd(unifiedNativeAd -> {
                    mNativeAds.add(new NativeAdWrapper(unifiedNativeAd, true));
                    if (!mAdLoader.isLoading()) {
                        if (listener != null) listener.onLoadFinished();
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        Log.e("[Loading ads]", "Failed to load with code: " + errorCode);
                        if (listener != null) listener.onLoadFailed(errorCode);
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder().build())
                .build();

        mAdLoader.loadAds(new AdRequest.Builder().build(), NUMBER_OF_ADS);
    }

    /**
     * Combines products with the latest loaded ads into a common list, while the ads are
     * distributed evenly.
     *
     * @param products list of products in which the latest loaded ads should be inserted
     * @return combined list with products and ads
     */
    public List<Object> combineWithLatestAds(List<ShowcaseItemModel> products) {
        return combine(products, mNativeAds);
    }

    private List<Object> combine(List<ShowcaseItemModel> products, List<NativeAdWrapper> ads) {
        mLatestAdPositions.clear();
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
                mLatestAdPositions.add(adIndex);
                adPointer++;
                adIndex += offset;
            } else if (productPointer < products.size()){
                items.add(products.get(productPointer));
                productPointer++;
            }
        }

        return items;
    }

    /**
     * @return the latest loaded ads of empty list if ads haven't been loaded yet
     */
    public List<NativeAdWrapper> getLatestAds() {
        return mNativeAds;
    }

    /**
     * If {@link #combineWithLatestAds(List)} has been used then the latest positions of the ads in
     * the combined list are known, so they are can be used for adapter notifying.
     *
     * @return positions of the ads in the combined list at latest combine operation.
     * If combine operation hasn't used then returns empty list.
     */
    public List<Integer> getLatestAdPositions() {
        return mLatestAdPositions;
    }

    /**
     * If {@link #combineWithLatestAds(List)} has been used then the latest positions of the ads in
     * the combined list are known, so they are with corresponding ads can be used to update
     * and notify adapter.
     *
     * @param isWideMode state of the ads which defines size mode
     * @return positions of the ads in the combined list at latest combine operation and
     * corresponding ads. If combine operation hasn't used then returns empty map.
     */
    public Map<Integer, NativeAdWrapper> getLatestAdsWithPositions(boolean isWideMode) {
        Map<Integer, NativeAdWrapper> map = new HashMap<>(mNativeAds.size());
        if (mLatestAdPositions.isEmpty() || mNativeAds.isEmpty()) return map;

        List<NativeAdWrapper> ads = withMode(mNativeAds, isWideMode);

        for (int i = 0; i < ads.size(); i++) {
            if (i >= mLatestAdPositions.size()) break;
            map.put(mLatestAdPositions.get(i), ads.get(i));
        }

        return map;
    }

    /**
     * Create copy of latest loaded ads with desired mode (wide or narrow).
     *
     * @param isWideMode state of the ads which defines size mode
     * @return copy of the latest loaded ads with desired size mode
     */
    public List<NativeAdWrapper> withMode(boolean isWideMode) {
        return withMode(mNativeAds, isWideMode);
    }

    private List<NativeAdWrapper> withMode(List<NativeAdWrapper> ads, boolean isWideMode) {
        List<NativeAdWrapper> newAds = new ArrayList<>(ads.size());
        for (NativeAdWrapper w : ads) {
            newAds.add(new NativeAdWrapper(w.getAd(), isWideMode));
        }
        return newAds;
    }
}
