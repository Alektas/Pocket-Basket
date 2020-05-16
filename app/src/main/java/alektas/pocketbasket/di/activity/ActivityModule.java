package alektas.pocketbasket.di.activity;

import androidx.annotation.NonNull;

import alektas.pocketbasket.ui.ActivityViewModel;
import alektas.pocketbasket.ui.DimensionsProvider;
import alektas.pocketbasket.ui.MainActivity;
import alektas.pocketbasket.ui.ViewModeDelegate;
import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {
    private MainActivity mActivity;

    public ActivityModule(@NonNull MainActivity activity) {
        mActivity = activity;
    }

    @ActivityScope
    @Provides
    public DimensionsProvider providesDimenstionsProvider() {
        return new DimensionsProvider(mActivity);
    }

    @ActivityScope
    @Provides
    public ViewModeDelegate providesViewModeDelegate(
            ActivityViewModel viewModel,
            DimensionsProvider dimensProvider
    ) {
        return new ViewModeDelegate(mActivity, viewModel, dimensProvider);
    }

}
