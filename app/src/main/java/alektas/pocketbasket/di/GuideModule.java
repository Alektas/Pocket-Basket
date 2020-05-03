package alektas.pocketbasket.di;

import javax.inject.Singleton;

import alektas.pocketbasket.guide.domain.ContextualGuide;
import alektas.pocketbasket.guide.domain.Guide;
import dagger.Module;
import dagger.Provides;

@Module
public class GuideModule {

    @Provides
    @Singleton
    Guide providesGuide() {
        return ContextualGuide.getInstance();
    }

}
