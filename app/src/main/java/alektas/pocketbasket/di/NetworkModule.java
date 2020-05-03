package alektas.pocketbasket.di;

import android.content.Context;

import javax.inject.Singleton;

import alektas.pocketbasket.R;
import alektas.pocketbasket.ads.AdManager;
import alektas.pocketbasket.utils.NetworkMonitor;
import dagger.Module;
import dagger.Provides;

@Module
public class NetworkModule {

    @Provides
    @Singleton
    NetworkMonitor providesNetworkMonitor(Context context) {
        return new NetworkMonitor(context);
    }

    @Provides
    @Singleton
    AdManager providesAdManager(Context context) {
        return new AdManager.Builder(context, R.string.ad_app_id, R.string.ad_unit_id)
                .withDebugAppId(R.string.ad_test_app_id)
                .withDebugAdId(R.string.ad_test_unit_id)
                .withTestDevice(R.string.ad_test_device_id)
                .build();
    }

}
