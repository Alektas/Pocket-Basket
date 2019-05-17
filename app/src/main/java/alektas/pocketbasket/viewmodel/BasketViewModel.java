package alektas.pocketbasket.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import alektas.pocketbasket.data.RepositoryImpl;
import alektas.pocketbasket.db.entities.Item;
import alektas.pocketbasket.domain.Repository;
import alektas.pocketbasket.domain.usecases.MoveBasketItem;
import alektas.pocketbasket.domain.usecases.UseCase;
import alektas.pocketbasket.guide.Guide;
import alektas.pocketbasket.guide.GuideContract;

public class BasketViewModel extends AndroidViewModel {
    private Repository mRepository;
    private Guide mGuide;
    private MutableLiveData<List<Item>> mBasketData = new MutableLiveData<>();

    public BasketViewModel(@NonNull Application application) {
        super(application);
        mRepository = RepositoryImpl.getInstance(application);
        mRepository.getBasketData().observe(mBasketData::setValue);
    }

    public void setGuide(Guide guide) {
        mGuide = guide;
    }

    public Guide getGuide() {
        return mGuide;
    }

    public LiveData<List<Item>> getBasketData() {
        return mBasketData;
    }

    /**
     * Caused by dragging items in the Basket.
     * Save items' positions in the Basket like a positions in the list.
     * @param names names of all basket items
     */
    public void updatePositions(List<String> names) {
        UseCase<List<String>, Void> useCase = new MoveBasketItem(mRepository);
        useCase.execute(names, null);
        mGuide.onCaseHappened(GuideContract.GUIDE_MOVE_ITEM);
    }

    /**
     * Check or uncheck item in the Basket
     */
    public void checkItem(String name) {
        mRepository.checkItem(name);
        mGuide.onCaseHappened(GuideContract.GUIDE_CHECK_ITEM);
    }

    /**
     * Verify if item in the Basket is checked.
     */
    public boolean isItemChecked(String name) {
        return mRepository.isChecked(name);
    }

    public void removeFromBasket(String name) {
        mRepository.removeFromBasket(name);
        mGuide.onCaseHappened(GuideContract.GUIDE_REMOVE_ITEM);
    }

}
