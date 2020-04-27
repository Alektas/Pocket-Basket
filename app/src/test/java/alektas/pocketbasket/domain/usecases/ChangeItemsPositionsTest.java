package alektas.pocketbasket.domain.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import alektas.pocketbasket.data.RepositoryImpl;
import alektas.pocketbasket.domain.Repository;
import alektas.pocketbasket.domain.entities.ItemModel;

import static org.mockito.Mockito.*;

@DisplayName("Use case of item position updating")
class ChangeItemsPositionsTest {
    private Repository mRepository;
    private UseCase<List<ItemModel>, Void> changePositionUseCase;

    @BeforeEach
    void setUp() {
        mRepository = mock(RepositoryImpl.class);
        changePositionUseCase = new ChangeItemsPositions(mRepository);
    }

    @Test
    @DisplayName("null -> not invoke updating positions")
    void execute_nullRequest_notInvokeUpdatingPositions() {
        changePositionUseCase.execute(null);

        verify(mRepository, never()).updateBasketPositions(anyList());
    }

    @Test
    @DisplayName("empty list -> not invoke updating positions")
    void execute_emptyRequest_notInvokeUpdatingPositions() {
        changePositionUseCase.execute(Collections.emptyList());

        verify(mRepository, never()).updateBasketPositions(anyList());
    }

    @Test
    @DisplayName("single item -> invoke changing position if item")
    void execute_singleItem_invokeUpdatingPosition() {
        ItemModel item1 = mock(ItemModel.class);
        when(item1.getKey()).thenReturn("Key1");
        changePositionUseCase.execute(Collections.singletonList(item1));

        verify(mRepository).updateBasketPositions(Collections.singletonList("Key1"));
    }

    @Test
    @DisplayName("item list -> invoke updating positions in correct order")
    void execute_listOfItems_correctUpdateKeysOrder() {
        ItemModel item1 = mock(ItemModel.class);
        when(item1.getKey()).thenReturn("Key1");
        ItemModel item2 = mock(ItemModel.class);
        when(item2.getKey()).thenReturn("Key2");
        changePositionUseCase.execute(Arrays.asList(item1, item2));

        verify(mRepository).updateBasketPositions(Arrays.asList("Key1", "Key2"));
    }
}