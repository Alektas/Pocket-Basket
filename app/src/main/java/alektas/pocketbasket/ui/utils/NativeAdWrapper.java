package alektas.pocketbasket.ui.utils;

import com.google.android.gms.ads.formats.UnifiedNativeAd;

public class NativeAdWrapper {
    private UnifiedNativeAd mAd;
    private boolean isIconShown = true;

    public NativeAdWrapper(UnifiedNativeAd ad) {
        mAd = ad;
    }

    public NativeAdWrapper(UnifiedNativeAd ad, boolean isIconShown) {
        mAd = ad;
        this.isIconShown = isIconShown;
    }

    public UnifiedNativeAd getAd() {
        return mAd;
    }

    public void setAd(UnifiedNativeAd ad) {
        mAd = ad;
    }

    public boolean isIconShown() {
        return isIconShown;
    }

    public void setIconShown(boolean iconShown) {
        isIconShown = iconShown;
    }
}
