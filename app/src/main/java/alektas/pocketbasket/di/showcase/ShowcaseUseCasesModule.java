package alektas.pocketbasket.di.showcase;

import java.util.List;

import javax.inject.Named;

import alektas.pocketbasket.data.db.entities.ShowcaseItem;
import alektas.pocketbasket.domain.usecases.AddItem;
import alektas.pocketbasket.domain.usecases.UseCase;
import alektas.pocketbasket.domain.usecases.showcase.DeleteSelectedShowcaseItems;
import alektas.pocketbasket.domain.usecases.showcase.GetDelMode;
import alektas.pocketbasket.domain.usecases.showcase.GetSelectedShowcaseItemCount;
import alektas.pocketbasket.domain.usecases.showcase.GetShowcase;
import alektas.pocketbasket.domain.usecases.showcase.ResetShowcase;
import alektas.pocketbasket.domain.usecases.showcase.SelectCategory;
import alektas.pocketbasket.domain.usecases.showcase.SelectShowcaseItem;
import alektas.pocketbasket.domain.usecases.showcase.SetDelMode;
import alektas.pocketbasket.domain.usecases.showcase.ToggleShowcaseItemSelection;
import alektas.pocketbasket.domain.usecases.showcase.UpdateItems;
import dagger.Binds;
import dagger.Module;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

@Module
public interface ShowcaseUseCasesModule {
    String ADD_ITEM = "ADD_ITEM";
    String RESET_SHOWCASE = "RESET_SHOWCASE";
    String SELECT_CATEGORY = "SELECT_CATEGORY";
    String SELECT_SHOWCASE_ITEM = "SELECT_SHOWCASE_ITEM";
    String UPDATE_ITEMS = "UPDATE_ITEMS";
    String SET_DEL_MODE = "SET_DEL_MODE";
    String GET_DEL_MODE = "GET_DEL_MODE";
    String GET_SELECTED_SHOWCASE_ITEM_COUNT = "GET_SELECTED_SHOWCASE_ITEM_COUNT";
    String GET_SHOWCASE = "GET_SHOWCASE";
    String DELETE_SELECTED_SHOWCASE_ITEMS = "DELETE_SELECTED_SHOWCASE_ITEMS";
    String TOGGLE_SHOWCASE_ITEM_SELECTION = "TOGGLE_SHOWCASE_ITEM_SELECTION";

    @Binds
    @Named(ADD_ITEM)
    UseCase<String, Single<Integer>> addItem(AddItem useCase);

    @Binds
    @Named(RESET_SHOWCASE)
    UseCase<Boolean, Completable> resetItems(ResetShowcase useCase);

    @Binds
    @Named(SELECT_CATEGORY)
    UseCase<String, Void> selectCategory(SelectCategory useCase);

    @Binds
    @Named(SELECT_SHOWCASE_ITEM)
    UseCase<String, Single<Integer>> selectShowcaseItem(SelectShowcaseItem useCase);

    @Binds
    @Named(UPDATE_ITEMS)
    UseCase<Void, Void> updateItems(UpdateItems useCase);

    @Binds
    @Named(DELETE_SELECTED_SHOWCASE_ITEMS)
    UseCase<Void, Completable> deleteSelectedShowcase(DeleteSelectedShowcaseItems useCase);

    @Binds
    @Named(GET_SHOWCASE)
    UseCase<Void, Observable<List<ShowcaseItem>>> getShowcaseItems(GetShowcase useCase);

    @Binds
    @Named(GET_SELECTED_SHOWCASE_ITEM_COUNT)
    UseCase<Void, Observable<Integer>> getDelItemsCount(GetSelectedShowcaseItemCount useCase);

    @Binds
    @Named(TOGGLE_SHOWCASE_ITEM_SELECTION)
    UseCase<ShowcaseItem, Void> toggleDeletingSelection(ToggleShowcaseItemSelection useCase);

    @Binds
    @Named(SET_DEL_MODE)
    UseCase<Boolean, Void> delMode(SetDelMode useCase);

    @Binds
    @Named(GET_DEL_MODE)
    UseCase<Void, Observable<Boolean>> getDelMode(GetDelMode useCase);

}
