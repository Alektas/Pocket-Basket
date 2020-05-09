package alektas.pocketbasket.data;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import alektas.pocketbasket.data.db.dao.BasketDao;
import alektas.pocketbasket.data.db.entities.BasketItem;
import alektas.pocketbasket.domain.BasketRepository;
import alektas.pocketbasket.domain.entities.CompletionException;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class BasketRepositoryImpl implements BasketRepository {
    private static final String TAG = "BasketRepositoryImpl";
    private BasketDao mBasketDao;

    @Inject
    public BasketRepositoryImpl(BasketDao basketDao) {
        mBasketDao = basketDao;
    }

    @Override
    public Observable<List<BasketItem>> getBasketData() {
        return mBasketDao.getItems()
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Boolean> isItemInBasket(String key) {
        return mBasketDao.getItemMeta(key)
                .subscribeOn(Schedulers.io())
                .flatMapSingle(meta -> Single.just(true))
                .onErrorReturn(error -> false);
    }

    @Override
    public Completable putToBasket(@NonNull String key) {
        return async(() -> mBasketDao.putToBasket(key));
    }

    @Override
    public void updateBasketPositions(List<String> keys) {
        launch(() -> mBasketDao.updatePositionsInOrderOf(keys));
    }

    @Override
    public void updateBasketItemPosition(String key, int position) {
        launch(() -> mBasketDao.updateItemPosition(key, position));
    }

    @Override
    public void toggleBasketItemCheck(@NonNull String key) {
        launch(() -> mBasketDao.toggleItemCheck(key));
    }

    @Override
    public void toggleBasketCheck() {
        launch(() -> mBasketDao.toggleBasketCheck());
    }

    @Override
    public Completable removeFromBasket(@NonNull String key) {
        return async(() -> mBasketDao.removeItem(key));
    }

    @Override
    public Completable removeItems(@NonNull Set<String> keys) {
        return async(() -> mBasketDao.removeItems(keys));
    }

    @SuppressLint("CheckResult")
    @Override
    public Completable removeCheckedBasketItems() {
        return Completable
                .create(emitter -> {
                    if (mBasketDao.removeCheckedItems()) {
                        emitter.onComplete();
                    } else {
                        emitter.onError(new CompletionException());
                    }
                })
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Completable cleanBasket() {
        return mBasketDao.cleanBasket()
                .subscribeOn(Schedulers.io());
    }


    /* Common methods */

    @SuppressLint("CheckResult")
    private void launch(Action action) {
        Completable.complete()
                .observeOn(Schedulers.io())
                .subscribe(action);
    }

    @SuppressLint("CheckResult")
    private Completable async(Action action) {
        Completable c = Completable.fromAction(action).subscribeOn(Schedulers.io()).cache();
        c.subscribe(
                () -> {/* empty */},
                error -> Log.e(TAG, "Error happened in async operation", error));
        return c;
    }

}