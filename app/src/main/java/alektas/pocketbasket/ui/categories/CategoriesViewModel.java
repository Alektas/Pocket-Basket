package alektas.pocketbasket.ui.categories;

import androidx.lifecycle.ViewModel;

import javax.inject.Inject;
import javax.inject.Named;

import alektas.pocketbasket.data.AppPreferences;
import alektas.pocketbasket.domain.usecases.UseCase;
import alektas.pocketbasket.ui.utils.LiveEvent;

import static alektas.pocketbasket.di.UseCasesModule.SELECT_CATEGORY;

public class CategoriesViewModel extends ViewModel {
    private UseCase<String, Void> mSelectCategory;
    private AppPreferences mPrefs;
    private LiveEvent<Integer> mInitCategory;

    @Inject
    public CategoriesViewModel(
            AppPreferences prefs,
            @Named(SELECT_CATEGORY) UseCase<String, Void> selectCategory
    ) {
        mPrefs = prefs;
        mSelectCategory = selectCategory;
        mInitCategory = new LiveEvent<>();
        mInitCategory.setValue(prefs.getSelectedCategoryId());
    }

    public void setCategory(String categoryKey) {
        mSelectCategory.execute(categoryKey);
    }

    public void saveCategory(int categoryId) {
        mPrefs.saveSelectedCategory(categoryId);
    }

    public LiveEvent<Integer> getInitCategory() {
        return mInitCategory;
    }
}
