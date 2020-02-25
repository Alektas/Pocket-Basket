package alektas.pocketbasket.ui.showcase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import alektas.pocketbasket.R;
import alektas.pocketbasket.databinding.ItemShowcaseBinding;
import alektas.pocketbasket.ui.utils.BaseRecyclerAdapter;
import alektas.pocketbasket.ui.ItemSizeProvider;
import alektas.pocketbasket.ads.NativeAdViewHolder;
import alektas.pocketbasket.ads.NativeAdWrapper;

public class ShowcaseRvAdapter extends BaseRecyclerAdapter {
    private static final int PRODUCT_VIEW_TYPE = 0;
    private static final int AD_VIEW_TYPE = 1;

    private ShowcaseViewModel mModel;
    private ItemSizeProvider mSizeProvider;

    public ShowcaseRvAdapter(@NonNull ShowcaseViewModel model) {
        super();
        mModel = model;
    }

    public ShowcaseRvAdapter(@NonNull ShowcaseViewModel model, ItemSizeProvider itemSizeProvider) {
        super();
        mModel = model;
        // Need to fix the item width
        // Width depends on the configuration (landscape or portrait)
        mSizeProvider = itemSizeProvider;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case AD_VIEW_TYPE:
                View nativeLayoutView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_ad_native, parent, false);
                return new NativeAdViewHolder(nativeLayoutView);
            case PRODUCT_VIEW_TYPE:
            default:
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_showcase, parent, false);
                itemView.getLayoutParams().width = mSizeProvider.getItemWidth();
                itemView.requestLayout();
                ItemShowcaseBinding binding = DataBindingUtil.bind(itemView);
                binding.setModel(mModel);
                return new ItemHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case AD_VIEW_TYPE:
                NativeAdViewHolder adHolder = (NativeAdViewHolder) viewHolder;
                NativeAdWrapper adWrapper = (NativeAdWrapper) getItems().get(position);
                displayNativeAd(adHolder, adWrapper);
                break;
            case PRODUCT_VIEW_TYPE:
            default:
                super.onBindViewHolder(viewHolder, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object item = getItems().get(position);
        if (item instanceof NativeAdWrapper) {
            return AD_VIEW_TYPE;
        }
        return PRODUCT_VIEW_TYPE;
    }

    private void displayNativeAd(NativeAdViewHolder holder, NativeAdWrapper adWrapper) {
        UnifiedNativeAdView adView = holder.getAdView();
        ((TextView) adView.getHeadlineView()).setText(adWrapper.getAd().getHeadline());

        NativeAd.Image icon = adWrapper.getAd().getIcon();
        ImageView iconView = (ImageView) adView.getIconView();
        if (adWrapper.isWideMode() && icon != null) {
            iconView.setImageDrawable(icon.getDrawable());
            iconView.setVisibility(View.VISIBLE);
        } else {
            iconView.setVisibility(View.GONE);
        }

        // Register ad and add it to the view
        adView.setNativeAd(adWrapper.getAd());
    }
}
