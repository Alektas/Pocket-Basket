package alektas.pocketbasket.di;

import java.util.List;

import javax.inject.Named;

import alektas.pocketbasket.data.db.entities.BasketItem;
import alektas.pocketbasket.data.db.entities.ShowcaseItem;
import alektas.pocketbasket.domain.Repository;
import alektas.pocketbasket.domain.entities.ItemModel;
import alektas.pocketbasket.domain.usecases.AddItem;
import alektas.pocketbasket.domain.usecases.ChangeBasketPositions;
import alektas.pocketbasket.domain.usecases.CleanBasket;
import alektas.pocketbasket.domain.usecases.DeleteSelectedShowcaseItems;
import alektas.pocketbasket.domain.usecases.GetBasket;
import alektas.pocketbasket.domain.usecases.GetDelMode;
import alektas.pocketbasket.domain.usecases.GetSelectedShowcaseItemCount;
import alektas.pocketbasket.domain.usecases.GetShowcase;
import alektas.pocketbasket.domain.usecases.GetViewMode;
import alektas.pocketbasket.domain.usecases.MoveBasketItemToTop;
import alektas.pocketbasket.domain.usecases.ResetShowcase;
import alektas.pocketbasket.domain.usecases.SetDelMode;
import alektas.pocketbasket.domain.usecases.ToggleBasketCheck;
import alektas.pocketbasket.domain.usecases.ToggleBasketItemCheck;
import alektas.pocketbasket.domain.usecases.RemoveCheckedBasketItems;
import alektas.pocketbasket.domain.usecases.RemoveBasketItem;
import alektas.pocketbasket.domain.usecases.SelectCategory;
import alektas.pocketbasket.domain.usecases.SelectShowcaseItem;
import alektas.pocketbasket.domain.usecases.SetViewMode;
import alektas.pocketbasket.domain.usecases.ToggleShowcaseItemSelection;
import alektas.pocketbasket.domain.usecases.UpdateItems;
import alektas.pocketbasket.domain.usecases.UseCase;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

@Module
public abstract class UseCasesModule {
    public static final String CLEAN_BASKET = "CLEAN_BASKET";
    public static final String REMOVE_BASKET_ITEM_BY_NAME = "REMOVE_BASKET_ITEM_BY_NAME";
    public static final String REMOVE_BASKET_ITEM_BY_KEY = "REMOVE_BASKET_ITEM_BY_KEY";
    public static final String ADD_ITEM = "ADD_ITEM";
    public static final String CHANGE_BASKET_POSITIONS = "CHANGE_BASKET_POSITIONS";
    public static final String TOGGLE_BASKET_CHECK = "TOGGLE_BASKET_CHECK";
    public static final String TOGGLE_BASKET_ITEM_CHECK = "TOGGLE_BASKET_ITEM_CHECK";
    public static final String MOVE_BASKET_ITEM_TO_TOP = "MOVE_BASKET_ITEM_TO_TOP";
    public static final String REMOVE_CHECKED_BASKET_ITEMS = "REMOVE_CHECKED_BASKET_ITEMS";
    public static final String RESET_SHOWCASE = "RESET_SHOWCASE";
    public static final String SELECT_CATEGORY = "SELECT_CATEGORY";
    public static final String SELECT_SHOWCASE_ITEM = "SELECT_SHOWCASE_ITEM";
    public static final String UPDATE_ITEMS = "UPDATE_ITEMS";
    public static final String SET_VIEW_MODE = "SET_VIEW_MODE";
    public static final String GET_VIEW_MODE = "GET_VIEW_MODE";
    public static final String SET_DEL_MODE = "SET_DEL_MODE";
    public static final String GET_DEL_MODE = "GET_DEL_MODE";
    public static final String GET_SELECTED_SHOWCASE_ITEM_COUNT = "GET_SELECTED_SHOWCASE_ITEM_COUNT";
    public static final String GET_SHOWCASE = "GET_SHOWCASE";
    public static final String GET_BASKET = "GET_BASKET";
    public static final String DELETE_SELECTED_SHOWCASE_ITEMS = "DELETE_SELECTED_SHOWCASE_ITEMS";
    public static final String TOGGLE_SHOWCASE_ITEM_SELECTION = "TOGGLE_SHOWCASE_ITEM_SELECTION";

    @Binds
    @Named(CLEAN_BASKET)
    abstract UseCase<Void, Completable> cleanBasket(CleanBasket useCase);

    @Provides
    @Named(REMOVE_BASKET_ITEM_BY_NAME)
    static UseCase<String, Completable> removeFromBasketByName(Repository repo) {
        return new RemoveBasketItem(repo, true);
    }

    @Provides
    @Named(REMOVE_BASKET_ITEM_BY_KEY)
    static UseCase<String, Completable> removeFromBasketByKey(Repository repo) {
        return new RemoveBasketItem(repo, false);
    }

    @Binds
    @Named(ADD_ITEM)
    abstract UseCase<String, Single<Integer>> addItem(AddItem useCase);

    @Binds
    @Named(CHANGE_BASKET_POSITIONS)
    abstract UseCase<List<ItemModel>, Void> changeBasketPositions(ChangeBasketPositions useCase);

    @Binds
    @Named(TOGGLE_BASKET_CHECK)
    abstract UseCase<Void, Void> toggleBasketCheck(ToggleBasketCheck useCase);

    @Binds
    @Named(TOGGLE_BASKET_ITEM_CHECK)
    abstract UseCase<String, Void> toggleBasketItemCheck(ToggleBasketItemCheck useCase);

    @Binds
    @Named(MOVE_BASKET_ITEM_TO_TOP)
    abstract UseCase<String, Void> moveItemToTop(MoveBasketItemToTop useCase);

    @Binds
    @Named(REMOVE_CHECKED_BASKET_ITEMS)
    abstract UseCase<Void, Completable> removeMarkedBasketItems(RemoveCheckedBasketItems useCase);

    @Binds
    @Named(RESET_SHOWCASE)
    abstract UseCase<Boolean, Completable> resetItems(ResetShowcase useCase);

    @Binds
    @Named(SELECT_CATEGORY)
    abstract UseCase<String, Void> selectCategory(SelectCategory useCase);

    @Binds
    @Named(SELECT_SHOWCASE_ITEM)
    abstract UseCase<String, Single<Integer>> selectShowcaseItem(SelectShowcaseItem useCase);

    @Binds
    @Named(UPDATE_ITEMS)
    abstract UseCase<Void, Void> updateItems(UpdateItems useCase);

    @Binds
    @Named(DELETE_SELECTED_SHOWCASE_ITEMS)
    abstract UseCase<Void, Void> deleteSelectedShowcase(DeleteSelectedShowcaseItems useCase);

    @Binds
    @Named(GET_BASKET)
    abstract UseCase<Void, Observable<List<BasketItem>>> getBasket(GetBasket useCase);

    @Binds
    @Named(GET_SHOWCASE)
    abstract UseCase<Void, Observable<List<ShowcaseItem>>> getShowcaseItems(GetShowcase useCase);

    @Binds
    @Named(GET_SELECTED_SHOWCASE_ITEM_COUNT)
    abstract UseCase<Void, Observable<Integer>> getDelItemsCount(GetSelectedShowcaseItemCount useCase);

    @Binds
    @Named(TOGGLE_SHOWCASE_ITEM_SELECTION)
    abstract UseCase<ShowcaseItem, Void> toggleDeletingSelection(ToggleShowcaseItemSelection useCase);

    @Binds
    @Named(SET_DEL_MODE)
    abstract UseCase<Boolean, Void> delMode(SetDelMode useCase);

    @Binds
    @Named(GET_DEL_MODE)
    abstract UseCase<Void, Observable<Boolean>> getDelMode(GetDelMode useCase);

    @Binds
    @Named(GET_VIEW_MODE)
    abstract UseCase<Void, Observable<Boolean>> getViewMode(GetViewMode useCase);

    @Binds
    @Named(SET_VIEW_MODE)
    abstract UseCase<Boolean, Void> setViewMode(SetViewMode useCase);

}
