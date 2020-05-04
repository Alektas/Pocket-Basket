package alektas.pocketbasket.domain.usecases;

import javax.inject.Inject;

import alektas.pocketbasket.domain.Repository;
import io.reactivex.Completable;

public class RemoveBasketItem implements UseCase<String, Completable> {
    private Repository mRepository;
    private boolean byName;

    @Inject
    public RemoveBasketItem(Repository repository, boolean byName) {
        mRepository = repository;
        this.byName = byName;
    }

    @Override
    public Completable execute(String s) {
        if (byName) {
            return mRepository.getItemByName(s)
                    .flatMapCompletable(item -> mRepository.removeFromBasket(item.getKey()));
        }
        return mRepository.removeFromBasket(s);
    }
}
