package alektas.pocketbasket.domain.usecases.showcase;

import java.util.List;

import javax.inject.Inject;

import alektas.pocketbasket.data.db.entities.ShowcaseItem;
import alektas.pocketbasket.domain.ShowcaseRepository;
import alektas.pocketbasket.domain.usecases.UseCase;
import io.reactivex.Observable;

public class GetShowcase implements UseCase<Void, Observable<List<ShowcaseItem>>> {
    private ShowcaseRepository mRepository;

    @Inject
    public GetShowcase(ShowcaseRepository repository) {
        mRepository = repository;
    }

    @Override
    public Observable<List<ShowcaseItem>> execute(Void request) {
        return mRepository.getShowcaseData();
    }

}
