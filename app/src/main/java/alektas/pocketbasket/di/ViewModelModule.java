package alektas.pocketbasket.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import alektas.pocketbasket.ui.ActivityViewModel;
import alektas.pocketbasket.ui.basket.BasketViewModel;
import alektas.pocketbasket.ui.categories.CategoriesViewModel;
import alektas.pocketbasket.ui.showcase.ShowcaseViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(ActivityViewModel.class)
    abstract ViewModel bindsActivityViewModel(ActivityViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ShowcaseViewModel.class)
    abstract ViewModel bindsShowcaseViewModel(ShowcaseViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(BasketViewModel.class)
    abstract ViewModel bindsBasketViewModel(BasketViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CategoriesViewModel.class)
    abstract ViewModel bindsCategoriesViewModel(CategoriesViewModel viewModel);

    @Binds
    abstract ViewModelProvider.Factory bindsViewModelFactory(ViewModelFactory factory);

}
