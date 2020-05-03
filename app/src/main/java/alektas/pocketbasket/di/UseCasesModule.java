package alektas.pocketbasket.di;

import java.util.List;

import javax.inject.Named;

import alektas.pocketbasket.domain.Repository;
import alektas.pocketbasket.domain.entities.ItemModel;
import alektas.pocketbasket.domain.usecases.AddItemUseCase;
import alektas.pocketbasket.domain.usecases.ChangeItemsPositions;
import alektas.pocketbasket.domain.usecases.CleanBasketUseCase;
import alektas.pocketbasket.domain.usecases.DelModeUseCase;
import alektas.pocketbasket.domain.usecases.MarkAllBasketItems;
import alektas.pocketbasket.domain.usecases.MarkBasketItem;
import alektas.pocketbasket.domain.usecases.MoveItemToTopUseCase;
import alektas.pocketbasket.domain.usecases.RemoveCheckedBasketItems;
import alektas.pocketbasket.domain.usecases.RemoveItemFromBasket;
import alektas.pocketbasket.domain.usecases.ResetItemsUseCase;
import alektas.pocketbasket.domain.usecases.SelectCategoryUseCase;
import alektas.pocketbasket.domain.usecases.SelectShowcaseItem;
import alektas.pocketbasket.domain.usecases.UpdateItemsUseCase;
import alektas.pocketbasket.domain.usecases.UseCase;
import dagger.Module;
import dagger.Provides;
import io.reactivex.Completable;
import io.reactivex.Single;

@Module
public class UseCasesModule {
    public static final String CLEAN_BASKET_USE_CASE = "CLEAN_BASKET_USE_CASE";
    public static final String REMOVE_BY_NAME_USE_CASE = "REMOVE_BY_NAME_USE_CASE";
    public static final String REMOVE_BY_KEY_USE_CASE = "REMOVE_BY_KEY_USE_CASE";
    public static final String ADD_ITEM_USE_CASE = "ADD_ITEM_USE_CASE";
    public static final String CHANGE_ITEM_POSITIONS_USE_CASE = "CHANGE_ITEM_POSITIONS_USE_CASE";
    public static final String DEL_MODE_USE_CASE = "DEL_MODE_USE_CASE";
    public static final String MARK_ALL_BASKET_USE_CASE = "MARK_ALL_BASKET_USE_CASE";
    public static final String MARK_BASKET_ITEM_USE_CASE = "MARK_BASKET_ITEM_USE_CASE";
    public static final String MOVE_ITEM_TO_TOP_USE_CASE = "MOVE_ITEM_TO_TOP_USE_CASE";
    public static final String REMOVE_MARKED_BASKET_ITEMS_USE_CASE = "REMOVE_MARKED_BASKET_ITEMS_USE_CASE";
    public static final String RESET_ITEMS_USE_CASE = "RESET_ITEMS_USE_CASE";
    public static final String SELECT_CATEGORY_USE_CASE = "SELECT_CATEGORY_USE_CASE";
    public static final String SELECT_SHOWCASE_ITEM_USE_CASE = "SELECT_SHOWCASE_ITEM_USE_CASE";
    public static final String UPDATE_ITEMS_USE_CASE = "UPDATE_ITEMS_USE_CASE";

    @Provides
    @Named(value = CLEAN_BASKET_USE_CASE)
    UseCase<Void, Completable> cleanBasket(Repository repo) {
        return new CleanBasketUseCase(repo);
    }

    @Provides
    @Named(value = REMOVE_BY_NAME_USE_CASE)
    UseCase<String, Completable> removeFromBasketByName(Repository repo) {
        return new RemoveItemFromBasket(repo, true);
    }

    @Provides
    @Named(value = REMOVE_BY_KEY_USE_CASE)
    UseCase<String, Completable> removeFromBasketByKey(Repository repo) {
        return new RemoveItemFromBasket(repo, false);
    }

    @Provides
    @Named(value = ADD_ITEM_USE_CASE)
    UseCase<String, Single<Integer>> addItem(Repository repo) {
        return new AddItemUseCase(repo);
    }

    @Provides
    @Named(value = CHANGE_ITEM_POSITIONS_USE_CASE)
    UseCase<List<ItemModel>, Void> changeItemPositions(Repository repo) {
        return new ChangeItemsPositions(repo);
    }

    @Provides
    @Named(value = DEL_MODE_USE_CASE)
    UseCase<Boolean, Void> delMode(Repository repo) {
        return new DelModeUseCase(repo);
    }

    @Provides
    @Named(value = MARK_ALL_BASKET_USE_CASE)
    UseCase<Void, Void> markAllBasket(Repository repo) {
        return new MarkAllBasketItems(repo);
    }

    @Provides
    @Named(value = MARK_BASKET_ITEM_USE_CASE)
    UseCase<String, Void> markBasketItem(Repository repo) {
        return new MarkBasketItem(repo);
    }

    @Provides
    @Named(value = MOVE_ITEM_TO_TOP_USE_CASE)
    UseCase<String, Void> moveItemToTop(Repository repo) {
        return new MoveItemToTopUseCase(repo);
    }

    @Provides
    @Named(value = REMOVE_MARKED_BASKET_ITEMS_USE_CASE)
    UseCase<Void, Completable> removeMarkedBasketItems(Repository repo) {
        return new RemoveCheckedBasketItems(repo);
    }

    @Provides
    @Named(value = RESET_ITEMS_USE_CASE)
    UseCase<Boolean, Completable> resetItems(Repository repo) {
        return new ResetItemsUseCase(repo);
    }

    @Provides
    @Named(value = SELECT_CATEGORY_USE_CASE)
    UseCase<String, Void> selectCategory(Repository repo) {
        return new SelectCategoryUseCase(repo);
    }

    @Provides
    @Named(value = SELECT_SHOWCASE_ITEM_USE_CASE)
    UseCase<String, Single<Integer>> selectShowcaseItem(Repository repo) {
        return new SelectShowcaseItem(repo);
    }

    @Provides
    @Named(value = UPDATE_ITEMS_USE_CASE)
    UseCase<Void, Void> updateItems(Repository repo) {
        return new UpdateItemsUseCase(repo);
    }

}
