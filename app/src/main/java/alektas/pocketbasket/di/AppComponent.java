package alektas.pocketbasket.di;

import android.content.Context;

import javax.inject.Singleton;

import alektas.pocketbasket.di.activity.ActivityComponent;
import alektas.pocketbasket.ui.searching.ItemsProvider;
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

    ActivityComponent.Factory activityComponentFactory();

    void inject(ItemsProvider provider);

    void inject(BasketWidgetService widgetService);

    void inject(BasketWidget widget);

}
