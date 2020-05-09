package alektas.pocketbasket.domain.usecases.showcase;

import java.util.Set;

import javax.inject.Inject;

import alektas.pocketbasket.domain.ShowcaseRepository;
import alektas.pocketbasket.domain.usecases.UseCase;
import io.reactivex.Observable;

public class GetSelectedShowcaseItemCount implements UseCase<Void, Observable<Integer>> {
    private ShowcaseRepository mRepository;

    @Inject
    public GetSelectedShowcaseItemCount(ShowcaseRepository repository) {
        mRepository = repository;
    }

    @Override
    public Observable<Integer> execute(Void request) {
        return mRepository.getSelectedItemsKeys().map(Set::size);
    }

}
