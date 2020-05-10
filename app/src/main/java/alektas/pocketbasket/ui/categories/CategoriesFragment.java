package alektas.pocketbasket.ui.categories;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import javax.inject.Inject;

import alektas.pocketbasket.App;
import alektas.pocketbasket.R;
import alektas.pocketbasket.utils.ResourcesUtils;

public class CategoriesFragment extends Fragment {
    @Inject
    CategoriesViewModel mViewModel;
    private RadioGroup mCategories;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        App.getComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCategories = view.findViewById(R.id.categories_radiogroup);
        mCategories.setOnCheckedChangeListener((group, checkedId) -> onCategorySelect(checkedId));

        observeModel();
    }

    private void observeModel() {
        mViewModel.getInitCategory().observe(getViewLifecycleOwner(), categoryId -> {
            if (categoryId == -1) return;
            mCategories.check(categoryId);
        });
    }

    @Override
    public void onStop() {
        mViewModel.saveCategory(mCategories.getCheckedRadioButtonId());
        super.onStop();
    }

    private void onCategorySelect(int buttonId) {
        switch (buttonId) {
            case R.id.all_rb:
                setCategory(R.string.all);
                break;
            case R.id.drink_rb:
                setCategory(R.string.drink);
                break;
            case R.id.fruits_rb:
                setCategory(R.string.fruit);
                break;
            case R.id.veg_rb:
                setCategory(R.string.vegetable);
                break;
            case R.id.groats_rb:
                setCategory(R.string.groats);
                break;
            case R.id.milky_rb:
                setCategory(R.string.milky);
                break;
            case R.id.floury_rb:
                setCategory(R.string.floury);
                break;
            case R.id.sweets_rb:
                setCategory(R.string.sweets);
                break;
            case R.id.meat_rb:
                setCategory(R.string.meat);
                break;
            case R.id.seafood_rb:
                setCategory(R.string.seafood);
                break;
            case R.id.semis_rb:
                setCategory(R.string.semis);
                break;
            case R.id.sauce_n_oil_rb:
                setCategory(R.string.sauce_n_oil);
                break;
            case R.id.household_rb:
                setCategory(R.string.household);
                break;
            case R.id.other_rb:
                setCategory(R.string.other);
                break;
        }
    }

    private void setCategory(int keyRes) {
        mViewModel.setCategory(ResourcesUtils.getResIdName(keyRes));
    }

}
