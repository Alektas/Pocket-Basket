package alektas.pocketbasket;

import android.content.Context;

import alektas.pocketbasket.view.MainActivity;
import dagger.Component;

@Component(modules = {AppModule.class})
public interface AppComponent {
    Context context();

    void inject(MainActivity activity);
}
