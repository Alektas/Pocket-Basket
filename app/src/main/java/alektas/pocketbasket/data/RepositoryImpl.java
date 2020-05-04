package alektas.pocketbasket.data;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import alektas.pocketbasket.R;
import alektas.pocketbasket.data.db.dao.BasketDao;
import alektas.pocketbasket.data.db.dao.ItemsDao;
import alektas.pocketbasket.data.db.dao.ShowcaseDao;
import alektas.pocketbasket.data.db.entities.BasketItem;
import alektas.pocketbasket.data.db.entities.Item;
import alektas.pocketbasket.data.db.entities.ShowcaseItem;
import alektas.pocketbasket.domain.Repository;
import alektas.pocketbasket.domain.entities.CompletionException;
import alektas.pocketbasket.utils.ResourcesUtils;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

public class RepositoryImpl implements Repository {
    private static final String TAG = "RepositoryImpl";
    private ItemsDao mItemsDao;
    private ShowcaseDao mShowcaseDao;
    private BasketDao mBasketDao;

    private CompositeDisposable mShowcaseDisposable;
    private BehaviorSubject<List<ShowcaseItem>> mShowcaseData;
    private BehaviorSubject<Boolean> viewModeState;
    private BehaviorSubject<Boolean> delModeState;

    /**
     * Items selected by the user for removal from the Showcase
     */
    private Map<String, ShowcaseItem> mDelItems;
    private BehaviorSubject<Integer> mDelItemsCountData;

    @Inject
    public RepositoryImpl(ItemsDao itemsDao, ShowcaseDao showcaseDao, BasketDao basketDao) {
        mItemsDao = itemsDao;
        mShowcaseDao = showcaseDao;
        mBasketDao = basketDao;
        mShowcaseData = BehaviorSubject.create();
        mDelItemsCountData = BehaviorSubject.create();
        viewModeState = BehaviorSubject.create();
        delModeState = BehaviorSubject.create();
        mDelItems = new HashMap<>();
        mShowcaseDisposable = new CompositeDisposable();
    }

    @Override
    public Observable<List<ShowcaseItem>> getShowcaseData() {
        return mShowcaseData;
    }

    @Override
    public Observable<List<BasketItem>> getBasketData() {
        return mBasketDao.getItems()
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Maybe<Item> getItemByName(String name) {
        return mItemsDao.getItemByName(name)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Boolean> observeViewMode() {
        return viewModeState;
    }

    @Override
    public Observable<Boolean> observeDelMode() {
        return delModeState;
    }

    @Override
    public Observable<Integer> getDelItemsCountData() {
        return mDelItemsCountData;
    }

    @Override
    public void setViewMode(boolean isShowcaseMode) {
        viewModeState.onNext(isShowcaseMode);
    }

    @Override
    public void setDelMode(boolean isDelMode) {
        mDelItems.clear();
        delModeState.onNext(isDelMode);
        if (!isDelMode) {
            mShowcaseData.onNext(mapDeletingSelections(mShowcaseData.getValue(), mDelItems));
        }
    }


    /* Basket methods */

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


    /* Showcase methods */

    @SuppressLint("CheckResult")
    @Override
    public void setCategory(String tag) {
        if (tag == null) return;

        mShowcaseDisposable.clear();
        if (tag.equals(ResourcesUtils.getResIdName(R.string.all))) {
            mShowcaseDisposable.add(mShowcaseDao.getShowcaseItems()
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::updateShowcase));
            return;
        }
        mShowcaseDisposable.add(mShowcaseDao.getShowcaseItems(tag)
                .subscribeOn(Schedulers.io())
                .subscribe(this::updateShowcase));
    }

    @Override
    public Completable resetShowcase() {
        return async(() -> mShowcaseDao.resetShowcase());
    }

    @Override
    public Completable restoreShowcase() {
        return async(() -> mShowcaseDao.restoreShowcase());
    }

    private void updateShowcase(List<ShowcaseItem> items) {
        if (delModeState.hasValue() && delModeState.getValue()) {
            updateDeletingSelections(items);
            return;
        }
        mShowcaseData.onNext(items);
    }

    /* Showcase items deleting */

    @Override
    public void toggleDeletingSelection(ShowcaseItem item) {
        if (item.isRemoval()) {
            mDelItems.remove(item.getKey());
        } else {
            mDelItems.put(item.getKey(), item);
        }
        mDelItemsCountData.onNext(mDelItems.size());
        updateDeletingSelections();
    }

    @Override
    public Completable deleteSelectedShowcaseItems() {
        return async(() -> {
            mShowcaseDao.deleteItems(mDelItems.values());
            mBasketDao.removeItems(mDelItems.keySet());
        });
    }

    private void updateDeletingSelections() {
        List<ShowcaseItem> items = mShowcaseData.getValue();
        if (items == null) return;
        updateDeletingSelections(items);
    }

    private void updateDeletingSelections(List<ShowcaseItem> items) {
        mShowcaseData.onNext(mapDeletingSelections(items, mDelItems));
    }

    private List<ShowcaseItem> mapDeletingSelections(
            List<ShowcaseItem> items,
            Map<String, ShowcaseItem> selectedItems
    ) {
        List<ShowcaseItem> updatedItems = new ArrayList<>(items.size());
        for (int i = 0; i < items.size(); i++) {
            ShowcaseItem item = items.get(i).copy();
            item.setRemoval(selectedItems.containsKey(item.getKey()));
            updatedItems.add(item);
        }
        return updatedItems;
    }


    /* Common methods */

    @Override
    public void createItem(String name) {
        launch(() -> {
            mItemsDao.createItem(name);
            mBasketDao.putToBasket(name);
        });
    }

    @SuppressLint("CheckResult")
    @Override
    public void updateDisplayedNames() {
        mItemsDao.getAllItems()
                .subscribeOn(Schedulers.io())
                .subscribe(items -> {
                    for (Item item : items) {
                        if (item.getNameRes() == null) continue;
                        item.setName(ResourcesUtils.getString(item.getNameRes()));
                    }
                    mItemsDao.update(items);
                });
    }

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