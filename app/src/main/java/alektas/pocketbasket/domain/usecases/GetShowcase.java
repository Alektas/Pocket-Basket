package alektas.pocketbasket.domain.usecases;

import java.util.List;

import javax.inject.Inject;

import alektas.pocketbasket.data.db.entities.BasketItem;
import alektas.pocketbasket.data.db.entities.ShowcaseItem;
import alektas.pocketbasket.domain.Repository;
import io.reactivex.Observable;

public class GetShowcase implements UseCase<Void, Observable<List<ShowcaseItem>>> {
    private Repository mRepository;

    @Inject
    public GetShowcase(Repository repository) {
        mRepository = repository;
    }

    @Override
    public Observable<List<ShowcaseItem>> execute(Void request) {
        return mRepository.getShowcaseData();
    }

}
