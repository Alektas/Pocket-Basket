package alektas.pocketbasket.ui.categories;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import javax.inject.Inject;

import alektas.pocketbasket.App;
import alektas.pocketbasket.R;

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
        mCategories.setOnCheckedChangeListener((group, checkedId) -> {
            mViewModel.onCategorySelect(checkedId);
        });

        observeModel();
    }

    private void observeModel() {
        mViewModel.getInitCategory().observe(getViewLifecycleOwner(), buttonId -> {
            RadioButton button = mCategories.findViewById(buttonId);
            button.setChecked(true);
        });
    }

    @Override
    public void onStop() {
        mViewModel.saveCategory(mCategories.getCheckedRadioButtonId());
        super.onStop();
    }

}
