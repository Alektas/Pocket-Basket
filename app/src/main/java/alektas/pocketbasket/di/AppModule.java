package alektas.pocketbasket.di;

import android.content.Context;

import androidx.annotation.NonNull;

import javax.inject.Singleton;

import alektas.pocketbasket.data.RepositoryImpl;
import alektas.pocketbasket.data.db.dao.ItemsDao;
import alektas.pocketbasket.domain.Repository;
import dagger.Module;
import dagger.Provides;

@Module
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
    Repository providesRepository(ItemsDao itemsDao) {
        return new RepositoryImpl(itemsDao);
    }

}
