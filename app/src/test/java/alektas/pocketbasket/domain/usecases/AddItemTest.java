package alektas.pocketbasket.domain.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import alektas.pocketbasket.data.RepositoryImpl;
import alektas.pocketbasket.data.db.entities.Item;
import alektas.pocketbasket.domain.Repository;
import io.reactivex.Maybe;
import io.reactivex.Single;

import static org.mockito.Mockito.*;

@DisplayName("Use case add basket items")
class AddItemTest {
    private UseCase<String, Single<Integer>> addItemUseCase;
    private Repository mRepository;

    @BeforeEach
    void setUpEach() {
        mRepository = mock(RepositoryImpl.class);
        addItemUseCase = new AddItem(mRepository);
    }

    @Test
    @DisplayName("null -> not invoke add or put item")
    void execute_nullRequest_callbackInvalidCode() {
        addItemUseCase.execute(null)
                .test()
                .assertValue(AddItem.ERROR_INVALID_NAME);

        verify(mRepository, never()).createItem(anyString());
        verify(mRepository, never()).putToBasket(anyString());
    }

    @Test
    @DisplayName("empty query -> not invoke add or put item")
    void execute_emptyRequest_callbackInvalidCode() {
        addItemUseCase.execute("")
                .test()
                .assertValue(AddItem.ERROR_INVALID_NAME);

        verify(mRepository, never()).createItem(anyString());
        verify(mRepository, never()).putToBasket(anyString());
    }

    @Test
    @DisplayName("new item one char name -> invoke adding new item, not putting existing")
    void execute_oneCharNewItemRequest_newItemAdded() {
        when(mRepository.getItemByName(anyString())).thenReturn(Maybe.empty());

        addItemUseCase.execute("a")
                .test()
                .assertValue(AddItem.NEW_ITEM_ADDED);

        verify(mRepository).createItem("a");
        verify(mRepository, never()).putToBasket(anyString());
    }

    @Test
    @DisplayName("existing item name -> invoke putting existing item, not adding new one")
    void execute_existingItemRequest_existingItemAdded() {
        Item item = mock(Item.class);
        when(item.getKey()).thenReturn("Key");
        when(mRepository.getItemByName("Item")).thenReturn(Maybe.just(item));

        addItemUseCase.execute("Item")
                .test()
                .assertValue(AddItem.EXISTING_ITEM_ADDED);

        verify(mRepository).putToBasket("Key");
        verify(mRepository, never()).createItem(anyString());
    }

    @Test
    @DisplayName("existing item name with lower case -> invoke putting existing item, not adding new one")
    void execute_existingItemLowerCaseRequest_existingItemAdded() {
        Item item = mock(Item.class);
        when(item.getKey()).thenReturn("Key");
        when(mRepository.getItemByName("Item")).thenReturn(Maybe.just(item));

        addItemUseCase.execute("item")
                .test()
                .assertValue(AddItem.EXISTING_ITEM_ADDED);

        verify(mRepository).putToBasket("Key");
        verify(mRepository, never()).createItem(anyString());
    }
}