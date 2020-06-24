package alektas.pocketbasket.data;

import android.annotation.SuppressLint;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import alektas.pocketbasket.data.db.LanguageCode;
import alektas.pocketbasket.data.db.dao.ShowcaseDao;
import alektas.pocketbasket.data.mappers.ItemMapper;
import alektas.pocketbasket.data.mappers.ShowcaseItemMapper;
import alektas.pocketbasket.domain.ShowcaseRepository;
import alektas.pocketbasket.domain.entities.Item;
import alektas.pocketbasket.domain.entities.ShowcaseItem;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

@Singleton
public class ShowcaseRepositoryImpl implements ShowcaseRepository {
    private static final String TAG = "RepositoryImpl";
    private LanguageCode mCurrentLanguage = LanguageCode.ENGLISH;
    private static final String CATEGORY_DEFAULT = "other";
    private static final String CATEGORY_ALL = "all";
    private ShowcaseDao mShowcaseDao;
    private CompositeDisposable mShowcaseDisposable;
    private BehaviorSubject<List<ShowcaseItem>> mShowcaseData;
    private BehaviorSubject<Boolean> mDelModeState;
    /**
     * Items selected by the user for removal from the Showcase
     */
    private Map<String, ShowcaseItem> mDelItems;
    private BehaviorSubject<Set<String>> mDelItemsData;

    @Inject
    public ShowcaseRepositoryImpl(ShowcaseDao showcaseDao) {
        mShowcaseDao = showcaseDao;
        mShowcaseData = BehaviorSubject.create();
        mDelItemsData = BehaviorSubject.create();
        mDelModeState = BehaviorSubject.create();
        mDelItems = new HashMap<>();
        mShowcaseDisposable = new CompositeDisposable();
    }

    @Override
    public Observable<List<ShowcaseItem>> getShowcaseData() {
        return mShowcaseData;
    }

    @Override
    public Observable<Boolean> observeDelMode() {
        return mDelModeState;
    }

    @Override
    public Observable<Set<String>> getSelectedItemsKeys() {
        return mDelItemsData;
    }

    @SuppressLint("CheckResult")
    @Override
    public void setCategory(String categoryKey) {
        mShowcaseDisposable.clear();
        if (categoryKey.equals(CATEGORY_ALL)) {
            mShowcaseDisposable.add(mShowcaseDao.getShowcaseItems(mCurrentLanguage.getCode())
                    .map(items -> new ShowcaseItemMapper().convert(items))
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::updateShowcase));
            return;
        }
        mShowcaseDisposable.add(mShowcaseDao.getShowcaseItems(categoryKey, mCurrentLanguage.getCode())
                .map(items -> new ShowcaseItemMapper().convert(items))
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
        if (mDelModeState.hasValue() && mDelModeState.getValue()) {
            updateDeletingSelections(items);
            return;
        }
        mShowcaseData.onNext(items);
    }

    @Override
    public Maybe<Item> getItemByName(String name) {
        return mShowcaseDao.getItemByName(name, mCurrentLanguage.getCode())
                .map(item -> new ItemMapper().convert(item))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Maybe<List<Item>> search(String query) {
        return mShowcaseDao.search(query, mCurrentLanguage.getCode())
                .map(items -> new ItemMapper().convert(items));
    }

    @Override
    public void changeLanguage(String language) {
        if (LanguageCode.RUSSIAN.getCode().equals(language)) {
            mCurrentLanguage = LanguageCode.RUSSIAN;
        } else {
            mCurrentLanguage = LanguageCode.ENGLISH;
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public Completable createItem(String name) {
        Completable c = Completable.fromAction(() -> {
            mShowcaseDao.createItem(name, CATEGORY_DEFAULT, mCurrentLanguage.getCode());
        })
                .subscribeOn(Schedulers.io())
                .cache();
        c.subscribe(
                () -> {/* empty */},
                error -> Log.e(TAG, "Error happened in async operation", error));
        return c;
    }

    /* Showcase items deleting */

    @Override
    public void setDelMode(boolean isDelMode) {
        mDelItems.clear();
        mDelModeState.onNext(isDelMode);
        if (!isDelMode) {
            mShowcaseData.onNext(mapDeletingSelections(mShowcaseData.getValue(), mDelItems));
        }
    }

    @Override
    public void toggleDeletingSelection(ShowcaseItem item) {
        if (item.isSelected()) {
            mDelItems.remove(item.getKey());
        } else {
            mDelItems.put(item.getKey(), item);
        }
        mDelItemsData.onNext(mDelItems.keySet());
        updateDeletingSelections();
    }

    @Override
    public Completable deleteSelectedShowcaseItems() {
        return async(() -> {
            List<String> keys = new ShowcaseItemMapper().getKeys(mDelItems.values());
            mShowcaseDao.deleteItems(keys);
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
            item.setSelected(selectedItems.containsKey(item.getKey()));
            updatedItems.add(item);
        }
        return updatedItems;
    }


    /* Common methods */

    @SuppressLint("CheckResult")
    private Completable async(Action action) {
        Completable c = Completable.fromAction(action).subscribeOn(Schedulers.io()).cache();
        c.subscribe(
                () -> {/* empty */},
                error -> Log.e(TAG, "Error happened in async operation", error));
        return c;
    }

}