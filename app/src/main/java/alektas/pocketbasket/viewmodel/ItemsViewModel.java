package alektas.pocketbasket.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import alektas.pocketbasket.App;
import alektas.pocketbasket.R;
import alektas.pocketbasket.db.entity.BasketItem;
import alektas.pocketbasket.db.entity.Item;

public class ItemsViewModel extends AndroidViewModel {
    private LiveData<List<Item>> mShowcaseData;
    private LiveData<List<BasketItem>> mBasketData;
    private Application mApp;

    public ItemsViewModel(@NonNull Application application) {
        super(application);
        mApp = application;
        insertItems();
    }

    public LiveData<List<Item>> getShowcaseData() {
        if (mShowcaseData == null) {
            mShowcaseData = ((App) mApp).getDatabase().getShowcaseDao().getAll();
        }
        return mShowcaseData;
    }

    public LiveData<List<BasketItem>> getBasketData() {
        if (mBasketData == null) {
            mBasketData = ((App) mApp).getDatabase().getBasketDao().getAll();
        }
        return mBasketData;
    }

    private void insertItems() {
        List<Item> items = getAllItems();
        ((App) mApp).getDatabase().getShowcaseDao().insertAll(items);
    }

    private List<Item> getAllItems() {
        List<Item> showcase = new ArrayList<>();
        addItem(showcase, R.string.soda, 0, R.string.drink);
        addItem(showcase, R.string.juice, 0, R.string.drink);
        addItem(showcase, R.string.tea, 0, R.string.drink);
        addItem(showcase, R.string.coffee, 0, R.string.drink);
        addItem(showcase, R.string.cacao, 0, R.string.drink);
        addItem(showcase, R.string.milk, R.drawable.ic_milk, R.string.milky, R.string.drink);
        addItem(showcase, R.string.curd, 0, R.string.milky);
        addItem(showcase, R.string.sour_cream, 0, R.string.milky);
        addItem(showcase, R.string.ice_cream, R.drawable.ice_cream, R.string.sweets);
        addItem(showcase, R.string.cookies, 0, R.string.sweets, R.string.floury);
        addItem(showcase, R.string.pie, 0, R.string.sweets, R.string.floury);
        addItem(showcase, R.string.cake, R.drawable.cake, R.string.sweets, R.string.floury);
        addItem(showcase, R.string.black_bread, R.drawable.bread, R.string.floury);
        addItem(showcase, R.string.white_bread, R.drawable.white_bread, R.string.floury);
        addItem(showcase, R.string.pasta, 0, R.string.floury);
        addItem(showcase, R.string.lemon, R.drawable.ic_lemon, R.string.fruit);
        addItem(showcase, R.string.apple, R.drawable.ic_apple, R.string.fruit);
        addItem(showcase, R.string.banana, R.drawable.ic_banana, R.string.fruit);
        addItem(showcase, R.string.orange, 0, R.string.fruit);
        addItem(showcase, R.string.potatoes, 0, R.string.vegetable);
        addItem(showcase, R.string.carrot, R.drawable.ic_carrot, R.string.vegetable);
        addItem(showcase, R.string.cabbage, R.drawable.ic_cabbage, R.string.vegetable);
        addItem(showcase, R.string.onion, R.drawable.onion, R.string.vegetable);
        addItem(showcase, R.string.tomato, 0, R.string.vegetable);
        addItem(showcase, R.string.cucumber, R.drawable.ic_cucumber, R.string.vegetable);
        addItem(showcase, R.string.rice, 0, R.string.groats);
        addItem(showcase, R.string.oatmeal, 0, R.string.groats);
        addItem(showcase, R.string.buckwheat, 0, R.string.groats);
        addItem(showcase, R.string.pork, R.drawable.meat, R.string.meat);
        addItem(showcase, R.string.chicken, R.drawable.chicken, R.string.meat);
        addItem(showcase, R.string.fish, R.drawable.fish, R.string.seafood);
        addItem(showcase, R.string.butter, 0, R.string.sauce_n_oil);
        addItem(showcase, R.string.seed_oil, R.drawable.oil, R.string.sauce_n_oil);
        addItem(showcase, R.string.mayo, 0, R.string.sauce_n_oil);
        addItem(showcase, R.string.ketchup, 0, R.string.sauce_n_oil);
        addItem(showcase, R.string.egg, R.drawable.egg, R.string.other);
        addItem(showcase, R.string.sugar, 0, R.string.sweets, R.string.other);
        addItem(showcase, R.string.salt, R.drawable.salt, R.string.other);
        addItem(showcase, R.string.toilet_paper, 0, R.string.household);
        addItem(showcase, R.string.shampoo, 0, R.string.household);
        addItem(showcase, R.string.soap, 0, R.string.household);
        addItem(showcase, R.string.detergent, 0, R.string.household);
        addItem(showcase, R.string.dumplings, 0, R.string.semis);
        addItem(showcase, R.string.cutlets, 0, R.string.semis);
        addItem(showcase, R.string.mushrooms, R.drawable.mushroom, R.string.other);
        addItem(showcase, R.string.cheese, R.drawable.cheese, R.string.milky);
        addItem(showcase, R.string.beef, R.drawable.beef, R.string.meat);
        addItem(showcase, R.string.greens, R.drawable.greens, R.string.vegetable);
        addItem(showcase, R.string.sausage, 0, R.string.meat);
        return showcase;
    }

    private void addItem(List<Item> items, int nameRes, int imgRes, int... tags) {
        Item item = new Item(nameRes, imgRes, tags);
        items.add(item);
    }
}
