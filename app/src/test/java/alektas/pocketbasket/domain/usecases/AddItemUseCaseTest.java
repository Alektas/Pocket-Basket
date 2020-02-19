package alektas.pocketbasket.domain.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import alektas.pocketbasket.data.RepositoryImpl;
import alektas.pocketbasket.domain.Repository;
import alektas.pocketbasket.domain.entities.ItemModel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@DisplayName("Use case add basket items")
class AddItemUseCaseTest {
    private UseCase<String, Integer> addItemUseCase;
    private Repository mRepository;

    @BeforeEach
    void setUpEach() {
        mRepository = mock(RepositoryImpl.class);
        addItemUseCase = new AddItemUseCase(mRepository);
    }

    @Test
    @DisplayName("null -> not invoke add or put item")
    void execute_nullRequest_callbackInvalidCode() {
        addItemUseCase.execute(null, result -> assertEquals(AddItemUseCase.INVALID_NAME, result));

        verify(mRepository, never()).addNewItem(anyString());
        verify(mRepository, never()).putToBasket(anyString());
    }

    @Test
    @DisplayName("empty query -> not invoke add or put item")
    void execute_emptyRequest_callbackInvalidCode() {
        addItemUseCase.execute("", result -> assertEquals(AddItemUseCase.INVALID_NAME, result));

        verify(mRepository, never()).addNewItem(anyString());
        verify(mRepository, never()).putToBasket(anyString());
    }

    @Test
    @DisplayName("new item one char name -> invoke adding new item, not putting existing")
    void execute_oneCharNewItemRequest_newItemAdded() {
        addItemUseCase.execute("a", result -> assertEquals(AddItemUseCase.NEW_ITEM_ADDED, result));

        verify(mRepository).addNewItem("a");
        verify(mRepository, never()).putToBasket(anyString());
    }

    @Test
    @DisplayName("existing item name -> invoke putting existing item, not adding new one")
    void execute_existingItemRequest_existingItemAdded() {
        ItemModel item = mock(ItemModel.class);
        when(item.getKey()).thenReturn("Key");
        when(mRepository.getItemByName("Item")).thenReturn(item);

        addItemUseCase.execute("Item", result -> assertEquals(AddItemUseCase.EXISTING_ITEM_ADDED, result));

        verify(mRepository).putToBasket("Key");
        verify(mRepository, never()).addNewItem(anyString());
    }

    @Test
    @DisplayName("existing item name with lower case -> invoke putting existing item, not adding new one")
    void execute_existingItemLowerCaseRequest_existingItemAdded() {
        ItemModel item = mock(ItemModel.class);
        when(item.getKey()).thenReturn("Key");
        when(mRepository.getItemByName("Item")).thenReturn(item);

        addItemUseCase.execute("item", result -> assertEquals(AddItemUseCase.EXISTING_ITEM_ADDED, result));

        verify(mRepository).putToBasket("Key");
        verify(mRepository, never()).addNewItem(anyString());
    }
}