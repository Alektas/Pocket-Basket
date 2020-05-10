package alektas.pocketbasket.ui.categories;

import androidx.annotation.IdRes;

import javax.inject.Inject;

import alektas.pocketbasket.R;
import alektas.pocketbasket.ui.PrefDefaults;
import alektas.pocketbasket.utils.ResourcesUtils;

public class CategoryMapper {

    @Inject
    public CategoryMapper() {}

    public String convertToCategoryKey(@IdRes int buttonId) {
        switch (buttonId) {
            case R.id.all_rb:
                return ResourcesUtils.getResIdName(R.string.all);
            case R.id.drink_rb:
                return ResourcesUtils.getResIdName(R.string.drink);
            case R.id.fruits_rb:
                return ResourcesUtils.getResIdName(R.string.fruit);
            case R.id.veg_rb:
                return ResourcesUtils.getResIdName(R.string.vegetable);
            case R.id.groats_rb:
                return ResourcesUtils.getResIdName(R.string.groats);
            case R.id.milky_rb:
                return ResourcesUtils.getResIdName(R.string.milky);
            case R.id.floury_rb:
                return ResourcesUtils.getResIdName(R.string.floury);
            case R.id.sweets_rb:
                return ResourcesUtils.getResIdName(R.string.sweets);
            case R.id.meat_rb:
                return ResourcesUtils.getResIdName(R.string.meat);
            case R.id.seafood_rb:
                return ResourcesUtils.getResIdName(R.string.seafood);
            case R.id.semis_rb:
                return ResourcesUtils.getResIdName(R.string.semis);
            case R.id.sauce_n_oil_rb:
                return ResourcesUtils.getResIdName(R.string.sauce_n_oil);
            case R.id.household_rb:
                return ResourcesUtils.getResIdName(R.string.household);
            case R.id.other_rb:
                return ResourcesUtils.getResIdName(R.string.other);
        }

        throw new IllegalArgumentException(
                "There is no category for the following button id: " + buttonId
        );
    }

}
