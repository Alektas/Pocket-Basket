package alektas.pocketbasket.ads;

import com.google.android.gms.ads.formats.UnifiedNativeAd;

/**
 * Wrapper to add some custom state to the UnifiedNativeAd objects.
 */
public class NativeAdWrapper {
    private UnifiedNativeAd mAd;
    private boolean isWideMode;

    public NativeAdWrapper(UnifiedNativeAd ad, boolean isWideMode) {
        mAd = ad;
        this.isWideMode = isWideMode;
    }

    public UnifiedNativeAd getAd() {
        return mAd;
    }

    public boolean isWideMode() {
        return isWideMode;
    }
}
