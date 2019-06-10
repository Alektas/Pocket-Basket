package alektas.pocketbasket.data;

import java.util.ArrayList;
import java.util.List;

import alektas.pocketbasket.R;
import alektas.pocketbasket.utils.ResourcesUtils;
import alektas.pocketbasket.data.db.entities.Item;

public class ItemGenerator {
    private static final String TAG = "ItemGenerator";

    private static List<Item> sItems;

    public static List<Item> getAll() {
        if (sItems != null) return sItems;
        sItems = new ArrayList<>();
        addItem(sItems, R.string.lemonade, R.drawable.ic_lemonade, R.string.drink);
        addItem(sItems, R.string.juice, R.drawable.ic_juice, R.string.drink);
        addItem(sItems, R.string.tea, R.drawable.ic_tea, R.string.drink);
        addItem(sItems, R.string.coffee, R.drawable.ic_coffee, R.string.drink);
        addItem(sItems, R.string.cacao, 0, R.string.drink);
        addItem(sItems, R.string.beer, 0, R.string.drink);
        addItem(sItems, R.string.wine, R.drawable.ic_wine, R.string.drink);
        addItem(sItems, R.string.vodka, 0, R.string.drink);
        addItem(sItems, R.string.chicory, 0, R.string.drink);
        addItem(sItems, R.string.water, R.drawable.ic_water, R.string.drink);

        addItem(sItems, R.string.milk, R.drawable.ic_milk, R.string.milky);
        addItem(sItems, R.string.curd, R.drawable.ic_curd, R.string.milky);
        addItem(sItems, R.string.sour_cream, R.drawable.ic_sourcream, R.string.milky);
        addItem(sItems, R.string.cheese, R.drawable.ic_cheese, R.string.milky);
        addItem(sItems, R.string.kefir, R.drawable.ic_kefir, R.string.milky);
        addItem(sItems, R.string.yogurt, R.drawable.ic_yogurt, R.string.milky);

        addItem(sItems, R.string.ice_cream, R.drawable.ic_icecream, R.string.sweets);
        addItem(sItems, R.string.cookies, R.drawable.ic_cookies, R.string.sweets);
        addItem(sItems, R.string.pie, 0, R.string.sweets);
        addItem(sItems, R.string.cake, 0, R.string.sweets);
        addItem(sItems, R.string.gingerbread, 0, R.string.sweets);
        addItem(sItems, R.string.waffles, R.drawable.ic_waffels, R.string.sweets);
        addItem(sItems, R.string.candies, R.drawable.ic_candies, R.string.sweets);
        addItem(sItems, R.string.sugar, R.drawable.ic_sugar, R.string.sweets);
        addItem(sItems, R.string.condensed_milk, 0, R.string.sweets);
        addItem(sItems, R.string.donuts, R.drawable.ic_donuts, R.string.sweets);

        addItem(sItems, R.string.brown_bread, R.drawable.ic_black_bread, R.string.floury);
        addItem(sItems, R.string.white_bread, R.drawable.ic_white_bread, R.string.floury);
        addItem(sItems, R.string.pasta, R.drawable.ic_pasta, R.string.floury);
        addItem(sItems, R.string.flour, 0, R.string.floury);
        addItem(sItems, R.string.pizza, R.drawable.ic_pizza, R.string.floury);
        addItem(sItems, R.string.burger, R.drawable.ic_burger, R.string.floury);
        addItem(sItems, R.string.hotdog, R.drawable.ic_hotdog, R.string.floury);

        addItem(sItems, R.string.lemon, R.drawable.ic_lemon, R.string.fruit);
        addItem(sItems, R.string.apple, R.drawable.ic_apple, R.string.fruit);
        addItem(sItems, R.string.banana, R.drawable.ic_banana, R.string.fruit);
        addItem(sItems, R.string.orange, R.drawable.ic_orange, R.string.fruit);
        addItem(sItems, R.string.grapes, R.drawable.ic_grapes, R.string.fruit);
        addItem(sItems, R.string.kiwi, 0, R.string.fruit);
        addItem(sItems, R.string.plums, 0, R.string.fruit);
        addItem(sItems, R.string.tangerines, 0, R.string.fruit);
        addItem(sItems, R.string.pears, 0, R.string.fruit);
        addItem(sItems, R.string.apricots, 0, R.string.fruit);
        addItem(sItems, R.string.peaches, 0, R.string.fruit);
        addItem(sItems, R.string.watermelon, R.drawable.ic_watermelon, R.string.fruit);

        addItem(sItems, R.string.potatoes, R.drawable.ic_potatoes, R.string.vegetable);
        addItem(sItems, R.string.carrot, R.drawable.ic_carrot, R.string.vegetable);
        addItem(sItems, R.string.cabbage, R.drawable.ic_cabbage, R.string.vegetable);
        addItem(sItems, R.string.onion, R.drawable.ic_onion, R.string.vegetable);
        addItem(sItems, R.string.tomato, R.drawable.ic_tomato, R.string.vegetable);
        addItem(sItems, R.string.cucumber, R.drawable.ic_cucumber, R.string.vegetable);
        addItem(sItems, R.string.garlic, R.drawable.ic_garlic, R.string.vegetable);
        addItem(sItems, R.string.pepper, R.drawable.ic_pepper, R.string.vegetable);
        addItem(sItems, R.string.greens, R.drawable.ic_greens, R.string.vegetable);
        addItem(sItems, R.string.corn, R.drawable.ic_corn, R.string.vegetable);

        addItem(sItems, R.string.rice, R.drawable.ic_rice, R.string.groats);
        addItem(sItems, R.string.oatmeal, 0, R.string.groats);
        addItem(sItems, R.string.buckwheat, R.drawable.ic_buckwheat, R.string.groats);
        addItem(sItems, R.string.peas, 0, R.string.groats);
        addItem(sItems, R.string.wheat_groats, R.drawable.ic_wheat, R.string.groats);
        addItem(sItems, R.string.millet_groats, 0, R.string.groats);
        addItem(sItems, R.string.corn_grits, 0, R.string.groats);
        addItem(sItems, R.string.barley_groats, 0, R.string.groats);

        addItem(sItems, R.string.pork, 0, R.string.meat);
        addItem(sItems, R.string.chicken, R.drawable.ic_chicken, R.string.meat);
        addItem(sItems, R.string.beef, R.drawable.ic_beef, R.string.meat);
        addItem(sItems, R.string.sausage, R.drawable.ic_sausage, R.string.meat);
        addItem(sItems, R.string.minced_meat, 0, R.string.meat);

        addItem(sItems, R.string.fish, R.drawable.ic_fish, R.string.seafood);
        addItem(sItems, R.string.crab, 0, R.string.seafood);
        addItem(sItems, R.string.shellfish, 0, R.string.seafood);
        addItem(sItems, R.string.shrimp, R.drawable.ic_shrimp, R.string.seafood);
        addItem(sItems, R.string.sushi, R.drawable.ic_sushi, R.string.seafood);
        addItem(sItems, R.string.fish_preserves, 0, R.string.seafood);
        addItem(sItems, R.string.caviar, 0, R.string.seafood);

        addItem(sItems, R.string.butter, R.drawable.ic_butter, R.string.sauce_n_oil);
        addItem(sItems, R.string.seed_oil, R.drawable.ic_oil, R.string.sauce_n_oil);
        addItem(sItems, R.string.mayo, 0, R.string.sauce_n_oil);
        addItem(sItems, R.string.ketchup, 0, R.string.sauce_n_oil);

        addItem(sItems, R.string.toilet_paper, 0, R.string.household);
        addItem(sItems, R.string.shampoo, 0, R.string.household);
        addItem(sItems, R.string.soap, 0, R.string.household);
        addItem(sItems, R.string.detergent, 0, R.string.household);
        addItem(sItems, R.string.sponges, 0, R.string.household);
        addItem(sItems, R.string.deodorant, 0, R.string.household);
        addItem(sItems, R.string.gloves, 0, R.string.household);
        addItem(sItems, R.string.rags, 0, R.string.household);
        addItem(sItems, R.string.toothpaste, 0, R.string.household);
        addItem(sItems, R.string.garbage_bags, 0, R.string.household);
        addItem(sItems, R.string.plastic_bags, 0, R.string.household);
        addItem(sItems, R.string.washing_powder, 0, R.string.household);
        addItem(sItems, R.string.soda, 0, R.string.household);
        addItem(sItems, R.string.wet_wipes, 0, R.string.household);
        addItem(sItems, R.string.shaving_razors, 0, R.string.household);
        addItem(sItems, R.string.shaving_foam, 0, R.string.household);
        addItem(sItems, R.string.mouth_rinse, 0, R.string.household);

        addItem(sItems, R.string.dumplings, R.drawable.ic_dumplings, R.string.semis);
        addItem(sItems, R.string.cutlets, 0, R.string.semis);
        addItem(sItems, R.string.canned_food, 0, R.string.semis);
        addItem(sItems, R.string.frozen_vegetables, 0, R.string.semis);

        addItem(sItems, R.string.mushrooms, R.drawable.ic_mushroom, R.string.other);
        addItem(sItems, R.string.eggs, R.drawable.ic_eggs, R.string.other);
        addItem(sItems, R.string.salt, R.drawable.ic_salt, R.string.other);
        addItem(sItems, R.string.cigarettes, 0, R.string.other);
        addItem(sItems, R.string.bubblegum, 0, R.string.other);
        addItem(sItems, R.string.sunflower_seeds, 0, R.string.other);
        addItem(sItems, R.string.chips, 0, R.string.other);

        return sItems;
    }

    private static void addItem(List<Item> items, int nameRes, int imgRes, int tagRes) {
        String nameResName = ResourcesUtils.getResIdName(nameRes);
        String tagResName = ResourcesUtils.getResIdName(tagRes);
        if (tagResName == null) tagResName = ResourcesUtils.getResIdName(R.string.other);

        String iconResName;
        if (imgRes == 0) iconResName = null;
        else iconResName = ResourcesUtils.getResIdName(imgRes);

        String name = ResourcesUtils.getString(nameRes);
        Item item = new Item(name, nameResName, iconResName, tagResName);

        items.add(item);
    }
}
