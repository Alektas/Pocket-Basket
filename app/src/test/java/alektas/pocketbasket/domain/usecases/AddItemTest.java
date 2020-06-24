package alektas.pocketbasket.domain.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import alektas.pocketbasket.data.BasketRepositoryImpl;
import alektas.pocketbasket.data.ShowcaseRepositoryImpl;
import alektas.pocketbasket.data.db.entities.ItemEntity;
import alektas.pocketbasket.domain.BasketRepository;
import alektas.pocketbasket.domain.ShowcaseRepository;
import io.reactivex.Maybe;
import io.reactivex.Single;

import static org.mockito.Mockito.*;

@DisplayName("Use case add basket items")
class AddItemTest {
    private UseCase<String, Single<Integer>> addItemUseCase;
    private ShowcaseRepository mShowcaseRepository;
    private BasketRepository mBasketRepository;

    @BeforeEach
    void setUpEach() {
        mShowcaseRepository = mock(ShowcaseRepositoryImpl.class);
        mBasketRepository = mock(BasketRepositoryImpl.class);
        addItemUseCase = new AddItem(mShowcaseRepository, mBasketRepository);
    }

    @Test
    @DisplayName("null -> not invoke add or put item")
    void execute_nullRequest_callbackInvalidCode() {
        addItemUseCase.execute(null)
                .test()
                .assertValue(AddItem.ERROR_INVALID_NAME);

        verify(mShowcaseRepository, never()).createItem(anyString());
        verify(mBasketRepository, never()).putToBasket(anyString());
    }

    @Test
    @DisplayName("empty query -> not invoke add or put item")
    void execute_emptyRequest_callbackInvalidCode() {
        addItemUseCase.execute("")
                .test()
                .assertValue(AddItem.ERROR_INVALID_NAME);

        verify(mShowcaseRepository, never()).createItem(anyString());
        verify(mBasketRepository, never()).putToBasket(anyString());
    }

    @Test
    @DisplayName("new item one char name -> invoke adding new item, not putting existing")
    void execute_oneCharNewItemRequest_newItemAdded() {
        when(mShowcaseRepository.getItemByName(anyString())).thenReturn(Maybe.empty());

        addItemUseCase.execute("a")
                .test()
                .assertValue(AddItem.NEW_ITEM_ADDED);

        verify(mShowcaseRepository).createItem("a");
        verify(mBasketRepository, never()).putToBasket(anyString());
    }

    @Test
    @DisplayName("existing item name -> invoke putting existing item, not adding new one")
    void execute_existingItemRequest_existingItemAdded() {
        ItemEntity item = mock(ItemEntity.class);
        when(item.getKey()).thenReturn("Key");
        when(mShowcaseRepository.getItemByName("Item")).thenReturn(Maybe.just(item));

        addItemUseCase.execute("Item")
                .test()
                .assertValue(AddItem.EXISTING_ITEM_ADDED);

        verify(mBasketRepository).putToBasket("Key");
        verify(mShowcaseRepository, never()).createItem(anyString());
    }

    @Test
    @DisplayName("existing item name with lower case -> invoke putting existing item, not adding new one")
    void execute_existingItemLowerCaseRequest_existingItemAdded() {
        ItemEntity item = mock(ItemEntity.class);
        when(item.getKey()).thenReturn("Key");
        when(mShowcaseRepository.getItemByName("Item")).thenReturn(Maybe.just(item));

        addItemUseCase.execute("item")
                .test()
                .assertValue(AddItem.EXISTING_ITEM_ADDED);

        verify(mBasketRepository).putToBasket("Key");
        verify(mShowcaseRepository, never()).createItem(anyString());
    }
}