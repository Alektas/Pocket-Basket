package alektas.pocketbasket.di;

import android.content.Context;

import javax.inject.Singleton;

import alektas.pocketbasket.ui.MainActivity;
import alektas.pocketbasket.ui.basket.BasketFragment;
import alektas.pocketbasket.ui.searching.ItemsProvider;
import alektas.pocketbasket.ui.showcase.ShowcaseFragment;
import alektas.pocketbasket.widget.BasketWidget;
import alektas.pocketbasket.widget.BasketWidgetService;
import dagger.Component;

@Singleton
@Component(modules = {
        AppModule.class, StorageModule.class, NetworkModule.class, GuideModule.class,
        UseCasesModule.class, ViewModelModule.class
})
public interface AppComponent {

    Context context();

    void inject(MainActivity activity);

    void inject(ItemsProvider provider);

    void inject(BasketWidget widget);

    void inject(BasketWidgetService widgetService);

    void inject(BasketFragment fragment);

    void inject(ShowcaseFragment fragment);

}
