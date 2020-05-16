package alektas.pocketbasket.ui.categories;

import androidx.lifecycle.ViewModel;

import javax.inject.Inject;
import javax.inject.Named;

import alektas.pocketbasket.data.AppPreferences;
import alektas.pocketbasket.domain.usecases.UseCase;
import alektas.pocketbasket.ui.utils.LiveEvent;

import static alektas.pocketbasket.di.showcase.ShowcaseUseCasesModule.SELECT_CATEGORY;

public class CategoriesViewModel extends ViewModel {
    private UseCase<String, Void> mSelectCategory;
    private AppPreferences mPrefs;
    private CategoryMapper mMapper;
    private LiveEvent<Integer> mInitCategory;

    @Inject
    public CategoriesViewModel(
            AppPreferences prefs,
            CategoryMapper mapper,
            @Named(SELECT_CATEGORY) UseCase<String, Void> selectCategory
    ) {
        mPrefs = prefs;
        mMapper = mapper;
        mSelectCategory = selectCategory;
        mInitCategory = new LiveEvent<>();
        mInitCategory.setValue(prefs.getSelectedCategoryId());
    }

    public void onCategorySelect(int buttonId) {
        String categoryKey = mMapper.convertToCategoryKey(buttonId);
        mSelectCategory.execute(categoryKey);
    }

    public void saveCategory(int categoryId) {
        mPrefs.saveSelectedCategory(categoryId);
    }

    public LiveEvent<Integer> getInitCategory() {
        return mInitCategory;
    }

}
