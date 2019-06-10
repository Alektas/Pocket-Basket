package alektas.pocketbasket;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import alektas.pocketbasket.ui.ActivityViewModel;
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
    ActivityViewModel viewModel(FragmentActivity activity) {
        return ViewModelProviders.of(activity).get(ActivityViewModel.class);
    }
}
