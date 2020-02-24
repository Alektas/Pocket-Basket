package alektas.pocketbasket.ui.utils;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import alektas.pocketbasket.R;

public class NativeAdViewHolder extends RecyclerView.ViewHolder {

    private UnifiedNativeAdView adView;

    public NativeAdViewHolder(View view) {
        super(view);
        adView = view.findViewById(R.id.ad_view);

        // Register the view used for each individual asset.
        adView.setHeadlineView(adView.findViewById(R.id.ad_item_title_wide));
        adView.setIconView(adView.findViewById(R.id.ad_item_icon));
    }

    public UnifiedNativeAdView getAdView() {
        return adView;
    }
}
