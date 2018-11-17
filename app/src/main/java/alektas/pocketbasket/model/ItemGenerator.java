package alektas.pocketbasket.model;

import android.content.res.Resources;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import alektas.pocketbasket.R;
import alektas.pocketbasket.Utils;
import alektas.pocketbasket.db.entity.Item;

public class ItemGenerator {
    private static final String TAG = "ItemGenerator";

    private static List<Item> sItems;

    public static List<Item> getAll() {
        if (sItems != null) return sItems;
        sItems = new ArrayList<>();
        addItem(sItems, R.string.lemonade, 0, R.string.drink);
        addItem(sItems, R.string.juice, 0, R.string.drink);
        addItem(sItems, R.string.tea, 0, R.string.drink);
        addItem(sItems, R.string.coffee, 0, R.string.drink);
        addItem(sItems, R.string.cacao, 0, R.string.drink);
        addItem(sItems, R.string.beer, 0, R.string.drink);
        addItem(sItems, R.string.wine, 0, R.string.drink);
        addItem(sItems, R.string.vodka, 0, R.string.drink);
        addItem(sItems, R.string.chicory, 0, R.string.drink);

        addItem(sItems, R.string.milk, R.drawable.ic_milk, R.string.milky);
        addItem(sItems, R.string.curd, 0, R.string.milky);
        addItem(sItems, R.string.sour_cream, 0, R.string.milky);
        addItem(sItems, R.string.cheese, R.drawable.ic_cheese, R.string.milky);

        addItem(sItems, R.string.ice_cream, R.drawable.ice_cream, R.string.sweets);
        addItem(sItems, R.string.cookies, R.drawable.ic_cookies, R.string.sweets);
        addItem(sItems, R.string.pie, 0, R.string.sweets);
        addItem(sItems, R.string.cake, R.drawable.cake, R.string.sweets);
        addItem(sItems, R.string.gingerbread, 0, R.string.sweets);
        addItem(sItems, R.string.waffles, 0, R.string.sweets);
        addItem(sItems, R.string.candies, R.drawable.candies, R.string.sweets);
        addItem(sItems, R.string.sugar, 0, R.string.sweets);

        addItem(sItems, R.string.brown_bread, R.drawable.bread, R.string.floury);
        addItem(sItems, R.string.white_bread, R.drawable.ic_white_bread, R.string.floury);
        addItem(sItems, R.string.pasta, R.drawable.ic_pasta, R.string.floury);

        addItem(sItems, R.string.lemon, R.drawable.ic_lemon, R.string.fruit);
        addItem(sItems, R.string.apple, R.drawable.ic_apple, R.string.fruit);
        addItem(sItems, R.string.banana, R.drawable.ic_banana, R.string.fruit);
        addItem(sItems, R.string.orange, 0, R.string.fruit);
        addItem(sItems, R.string.grapes, R.drawable.ic_grapes, R.string.fruit);

        addItem(sItems, R.string.potatoes, 0, R.string.vegetable);
        addItem(sItems, R.string.carrot, R.drawable.ic_carrot, R.string.vegetable);
        addItem(sItems, R.string.cabbage, R.drawable.ic_cabbage, R.string.vegetable);
        addItem(sItems, R.string.onion, R.drawable.ic_onion, R.string.vegetable);
        addItem(sItems, R.string.tomato, 0, R.string.vegetable);
        addItem(sItems, R.string.cucumber, R.drawable.ic_cucumber, R.string.vegetable);
        addItem(sItems, R.string.garlic, R.drawable.ic_garlic, R.string.vegetable);
        addItem(sItems, R.string.pepper, R.drawable.ic_pepper, R.string.vegetable);
        addItem(sItems, R.string.greens, R.drawable.ic_greens, R.string.vegetable);

        addItem(sItems, R.string.rice, R.drawable.ic_rice, R.string.groats);
        addItem(sItems, R.string.oatmeal, 0, R.string.groats);
        addItem(sItems, R.string.buckwheat, 0, R.string.groats);
        addItem(sItems, R.string.peas, 0, R.string.groats);

        addItem(sItems, R.string.pork, R.drawable.meat, R.string.meat);
        addItem(sItems, R.string.chicken, R.drawable.chicken, R.string.meat);
        addItem(sItems, R.string.beef, R.drawable.ic_beef, R.string.meat);
        addItem(sItems, R.string.sausage, R.drawable.ic_sausage, R.string.meat);

        addItem(sItems, R.string.fish, R.drawable.ic_fish, R.string.seafood);

        addItem(sItems, R.string.butter, 0, R.string.sauce_n_oil);
        addItem(sItems, R.string.seed_oil, R.drawable.oil, R.string.sauce_n_oil);
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

        addItem(sItems, R.string.dumplings, 0, R.string.semis);
        addItem(sItems, R.string.cutlets, 0, R.string.semis);
        addItem(sItems, R.string.canned_food, 0, R.string.semis);

        addItem(sItems, R.string.mushrooms, R.drawable.ic_mushroom, R.string.other);
        addItem(sItems, R.string.egg, R.drawable.egg, R.string.other);
        addItem(sItems, R.string.salt, R.drawable.ic_salt, R.string.other);
        addItem(sItems, R.string.cigarettes, 0, R.string.other);
        addItem(sItems, R.string.soda, 0, R.string.other);

        return sItems;
    }

    private static void addItem(List<Item> items, int nameRes, int imgRes, int tags) {
        Item item;
        try {
            String name = Utils.getString(nameRes);
            item = new Item(name, nameRes, imgRes, tags);
        }
        catch (Resources.NotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "addItem: item has no image resource.", e);
            item = new Item(nameRes, imgRes, tags);
        }
        items.add(item);
    }
}
