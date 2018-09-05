package alektas.pocketbasket.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import alektas.pocketbasket.R;

public class ShowcaseRepo implements Repository {
    private List<Data> mShowcase;

    public ShowcaseRepo() {
        mShowcase = new ArrayList<>();
        addItem(R.string.soda, 0, R.string.drink);
        addItem(R.string.juice, 0, R.string.drink);
        addItem(R.string.tea, 0, R.string.drink);
        addItem(R.string.coffee, 0, R.string.drink);
        addItem(R.string.cacao, 0, R.string.drink);
        addItem(R.string.milk, R.drawable.ic_milk, R.string.drink, R.string.milky);
        addItem(R.string.curd, 0, R.string.milky);
        addItem(R.string.sour_cream, 0, R.string.milky);
        addItem(R.string.ice_cream, R.drawable.ice_cream, R.string.sweets);
        addItem(R.string.cookies, 0, R.string.sweets, R.string.floury);
        addItem(R.string.pie, 0, R.string.sweets, R.string.floury);
        addItem(R.string.cake, R.drawable.cake, R.string.sweets, R.string.floury);
        addItem(R.string.black_bread, R.drawable.bread, R.string.floury);
        addItem(R.string.white_bread, R.drawable.white_bread, R.string.floury);
        addItem(R.string.pasta, 0, R.string.floury);
        addItem(R.string.lemon, R.drawable.ic_lemon, R.string.fruit);
        addItem(R.string.apple, R.drawable.ic_apple, R.string.fruit);
        addItem(R.string.banana, R.drawable.ic_banana, R.string.fruit);
        addItem(R.string.orange, 0, R.string.fruit);
        addItem(R.string.potatoes, 0, R.string.vegetable);
        addItem(R.string.carrot, R.drawable.ic_carrot, R.string.vegetable);
        addItem(R.string.cabbage, R.drawable.ic_cabbage, R.string.vegetable);
        addItem(R.string.onion, R.drawable.onion, R.string.vegetable);
        addItem(R.string.tomato, 0, R.string.vegetable);
        addItem(R.string.cucumber, R.drawable.ic_cucumber, R.string.vegetable);
        addItem(R.string.rice, 0, R.string.groats);
        addItem(R.string.oatmeal, 0, R.string.groats);
        addItem(R.string.buckwheat, 0, R.string.groats);
        addItem(R.string.pork, R.drawable.meat, R.string.meat);
        addItem(R.string.chicken, R.drawable.chicken, R.string.meat);
        addItem(R.string.fish, R.drawable.fish, R.string.seafood);
        addItem(R.string.butter, 0, R.string.sauce_n_oil);
        addItem(R.string.seed_oil, R.drawable.oil, R.string.sauce_n_oil);
        addItem(R.string.mayo, 0, R.string.sauce_n_oil);
        addItem(R.string.ketchup, 0, R.string.sauce_n_oil);
        addItem(R.string.egg, R.drawable.egg, R.string.other);
        addItem(R.string.sugar, 0, R.string.sweets, R.string.other);
        addItem(R.string.salt, R.drawable.salt, R.string.other);
        addItem(R.string.toilet_paper, 0, R.string.household);
        addItem(R.string.shampoo, 0, R.string.household);
        addItem(R.string.soap, 0, R.string.household);
        addItem(R.string.detergent, 0, R.string.household);
        addItem(R.string.dumplings, 0, R.string.semis);
        addItem(R.string.cutlets, 0, R.string.semis);
        addItem(R.string.mushrooms, R.drawable.mushroom, R.string.other);
        addItem(R.string.cheese, R.drawable.cheese, R.string.milky);
        addItem(R.string.beef, R.drawable.beef, R.string.meat);
        addItem(R.string.greens, R.drawable.greens, R.string.vegetable);
        addItem(R.string.sausage, 0, R.string.meat);
    }

    private void addItem(int nameRes, int imgRes, int... tags) {
        Item item = new Item(nameRes, imgRes, tags);
        mShowcase.add(item);
    }

    @Override
    public void addData(Data data) {
        mShowcase.add(data);
    }

    @Override
    public void deleteData(String key) {
        for (Iterator<Data> iterator = mShowcase.iterator(); iterator.hasNext(); ) {
            Data item = iterator.next();
            if (item.getKey().equals(key)) iterator.remove();
        }
    }

    @Override
    public void clear() {
        mShowcase.clear();
    }

    @Override
    public Data getData(String key) {
        for (Data item : mShowcase) {
            if (item.getKey().equals(key)) return item;
        }
        return null;
    }

    @Override
    public List<Data> getAll() {
        return mShowcase;
    }
}
