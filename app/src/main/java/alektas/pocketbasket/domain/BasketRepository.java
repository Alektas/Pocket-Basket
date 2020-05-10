package alektas.pocketbasket.domain;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Set;

import alektas.pocketbasket.data.db.entities.BasketItem;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

public interface BasketRepository {

    /**
     * Data contains only items stored in the basket.
     */
    Observable<List<BasketItem>> getBasketData();
    Single<Boolean> isItemInBasket(String key);
    Completable putToBasket(String key);
    Completable removeFromBasket(String key);
    void updateBasketPositions(List<String> keys);
    void updateBasketItemPosition(String key, int position);
    /**
     * Change item state in "Basket"
     */
    void toggleBasketItemCheck(String key);
    /**
     * Check all items in Basket (or uncheck if already all items are checked)
     */
    void toggleBasketCheck();

    Completable removeItems(@NonNull Set<String> keys);

    Completable removeCheckedBasketItems();
    Completable cleanBasket();

}