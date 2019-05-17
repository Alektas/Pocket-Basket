package alektas.pocketbasket.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import alektas.pocketbasket.domain.Repository;
import alektas.pocketbasket.data.RepositoryImpl;


public class CategoryViewModel extends AndroidViewModel {
    private Repository mRepository;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        mRepository = RepositoryImpl.getInstance(application);
    }
}
