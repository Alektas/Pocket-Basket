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
import java.util.List;

import alektas.pocketbasket.BuildConfig;
import alektas.pocketbasket.data.db.entities.ShowcaseItem;

public class AdManager {
    private static final int MIN_OFFSET_OF_ADS = 8;
    private static final int NUMBER_OF_ADS = 5;
    private List<NativeAdWrapper> mNativeAds = new ArrayList<>();
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
     *
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
    public List<Object> combineWithLatestAds(List<ShowcaseItem> products) {
        return combine(products, mNativeAds, MIN_OFFSET_OF_ADS);
    }

    /**
     * Combines products with ads into a common list, while the ads are
     * distributed evenly.
     *
     * @param products     list of products in which the latest loaded ads should be inserted
     * @param ads          list of ads which should be inserted into the products
     * @param minAdsOffset minimal intervals between the ads. If the list of products is small,
     *                     it allows to not overload the list with ads.
     * @return combined list with products and ads
     */
    public List<Object> combine(List<ShowcaseItem> products, List<NativeAdWrapper> ads, int minAdsOffset) {
        if (ads.size() <= 0) return new ArrayList<>(products);

        List<Object> combined = new ArrayList<>(products);

        int offset = Math.max((products.size() / ads.size() + 1), minAdsOffset);
        int adIndex = 0;
        for (NativeAdWrapper ad : ads) {
            if (adIndex >= combined.size()) break;
            combined.add(adIndex, ad);
            adIndex += offset;
        }

        return combined;
    }
}
