package alektas.pocketbasket.di;

import android.content.Context;

import androidx.annotation.NonNull;

import javax.inject.Singleton;

import alektas.pocketbasket.data.AppPreferences;
import alektas.pocketbasket.data.AppRepositoryImpl;
import alektas.pocketbasket.data.BasketRepositoryImpl;
import alektas.pocketbasket.data.ShowcaseRepositoryImpl;
import alektas.pocketbasket.data.db.dao.BasketDao;
import alektas.pocketbasket.data.db.dao.ShowcaseDao;
import alektas.pocketbasket.di.activity.ActivityComponent;
import alektas.pocketbasket.domain.AppRepository;
import alektas.pocketbasket.domain.BasketRepository;
import alektas.pocketbasket.domain.ShowcaseRepository;
import dagger.Module;
import dagger.Provides;

@Module(subcomponents = ActivityComponent.class)
public class AppModule {
    private Context mContext;

    public AppModule(@NonNull Context context) {
        mContext = context;
    }

    @Provides
    Context context() {
        return mContext.getApplicationContext();
    }

    @Provides
    @Singleton
    AppRepository providesRepository(AppPreferences prefs) {
        return new AppRepositoryImpl(prefs);
    }

    @Provides
    @Singleton
    BasketRepository providesBasketRepository(BasketDao basketDao) {
        return new BasketRepositoryImpl(basketDao);
    }

    @Provides
    @Singleton
    ShowcaseRepository providesShowcaseRepository(ShowcaseDao showcaseDao) {
        return new ShowcaseRepositoryImpl(showcaseDao);
    }

}
