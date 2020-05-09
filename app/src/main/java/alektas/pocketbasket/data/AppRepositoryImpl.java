package alektas.pocketbasket.data;

import javax.inject.Inject;

import alektas.pocketbasket.domain.AppRepository;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public class AppRepositoryImpl implements AppRepository {
    private static final String TAG = "RepositoryImpl";
    private BehaviorSubject<Boolean> viewModeState;

    @Inject
    public AppRepositoryImpl() {
        viewModeState = BehaviorSubject.create();
    }

    @Override
    public Observable<Boolean> observeViewMode() {
        return viewModeState;
    }

    @Override
    public void setViewMode(boolean isShowcaseMode) {
        viewModeState.onNext(isShowcaseMode);
    }

}