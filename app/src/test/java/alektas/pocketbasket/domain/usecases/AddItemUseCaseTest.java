package alektas.pocketbasket.domain.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import alektas.pocketbasket.data.RepositoryImpl;
import alektas.pocketbasket.domain.Repository;
import alektas.pocketbasket.domain.entities.ItemModel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AddItemUseCaseTest {
    private UseCase<String, Integer> addItemUseCase;
    private Repository mRepository;

    @BeforeEach
    void setUpEach() {
        mRepository = mock(RepositoryImpl.class);
        addItemUseCase = new AddItemUseCase(mRepository);
    }

    @Test
    void execute_nullRequest_callbackInvalidCode() {
        addItemUseCase.execute(null, result -> assertEquals(AddItemUseCase.INVALID_NAME, result));

        verify(mRepository, never()).addNewItem(anyString());
        verify(mRepository, never()).putToBasket(anyString());
    }

    @Test
    void execute_emptyRequest_callbackInvalidCode() {
        addItemUseCase.execute("", result -> assertEquals(AddItemUseCase.INVALID_NAME, result));

        verify(mRepository, never()).addNewItem(anyString());
        verify(mRepository, never()).putToBasket(anyString());
    }

    @Test
    void execute_oneCharNewItemRequest_newItemAdded() {
        addItemUseCase.execute("a", result -> assertEquals(AddItemUseCase.NEW_ITEM_ADDED, result));

        verify(mRepository).addNewItem("a");
        verify(mRepository, never()).putToBasket(anyString());
    }

    @Test
    void execute_existingItemRequest_existingItemAdded() {
        ItemModel item = mock(ItemModel.class);
        when(item.getKey()).thenReturn("Key");
        when(mRepository.getItemByName("Item")).thenReturn(item);

        addItemUseCase.execute("Item", result -> assertEquals(AddItemUseCase.EXISTING_ITEM_ADDED, result));

        verify(mRepository).putToBasket("Key");
        verify(mRepository, never()).addNewItem(anyString());
    }

    @Test
    void execute_existingItemLowerCaseRequest_existingItemAdded() {
        ItemModel item = mock(ItemModel.class);
        when(item.getKey()).thenReturn("Key");
        when(mRepository.getItemByName("Item")).thenReturn(item);

        addItemUseCase.execute("item", result -> assertEquals(AddItemUseCase.EXISTING_ITEM_ADDED, result));

        verify(mRepository).putToBasket("Key");
        verify(mRepository, never()).addNewItem(anyString());
    }
}