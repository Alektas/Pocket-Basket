package alektas.pocketbasket.di;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private Map<Class<? extends ViewModel>, Provider<ViewModel>> mViewModels;

    @Inject
    ViewModelFactory(Map<Class<? extends ViewModel>, Provider<ViewModel>> viewModels) {
        mViewModels = viewModels;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            T vm = (T) mViewModels.get(modelClass).get();
            if (vm == null) {
                throw new IllegalArgumentException("Cannot find view model " + modelClass + ". \n" +
                        "You must add the following code to your @Subcomponent's @Module: \n" +
                        "@Binds \n" +
                        "@IntoMap \n" +
                        "@ViewModelKey(" + modelClass + ".class) \n" +
                        "ViewModel bind" + modelClass + "(" + modelClass + " viewModel)");
            }
            return vm;
    }

}
