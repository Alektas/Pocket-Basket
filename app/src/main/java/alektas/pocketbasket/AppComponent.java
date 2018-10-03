package alektas.pocketbasket;

import alektas.pocketbasket.view.MainActivity;
import dagger.Component;

@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(MainActivity activity);
}
