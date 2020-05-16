package alektas.pocketbasket.di.activity;

import alektas.pocketbasket.di.basket.BasketUseCasesModule;
import alektas.pocketbasket.di.showcase.ShowcaseUseCasesModule;
import alektas.pocketbasket.ui.MainActivity;
import alektas.pocketbasket.ui.basket.BasketFragment;
import alektas.pocketbasket.ui.categories.CategoriesFragment;
import alektas.pocketbasket.ui.searching.ItemsProvider;
import alektas.pocketbasket.ui.showcase.ShowcaseFragment;
import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = {
        ActivityModule.class, ShowcaseUseCasesModule.class, BasketUseCasesModule.class
})
public interface ActivityComponent {

    @Subcomponent.Factory
    interface Factory {
        ActivityComponent create(ActivityModule module);
    }

    void inject(MainActivity activity);

    void inject(ItemsProvider provider);

    void inject(BasketFragment fragment);

    void inject(ShowcaseFragment fragment);

    void inject(CategoriesFragment fragment);

}
