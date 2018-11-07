package alektas.pocketbasket;

import android.content.Context;

import alektas.pocketbasket.viewmodel.ItemsViewModel;
import androidx.annotation.NonNull;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private App mApp;

    AppModule(@NonNull App app) {
        mApp = app;
    }

    @Provides
    Context context() {
        return mApp.getApplicationContext();
    }

    @Provides
    ItemsViewModel viewModel(FragmentActivity activity) {
        return ViewModelProviders.of(activity).get(ItemsViewModel.class);
    }
}
