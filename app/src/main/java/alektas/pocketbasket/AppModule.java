package alektas.pocketbasket;

import android.content.Context;
import android.support.annotation.NonNull;

import dagger.Module;

@Module
public class AppModule {
    Context mContext;

    AppModule(@NonNull Context context) {
        mContext = context;
    }

}
