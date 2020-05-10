package alektas.pocketbasket.data;

import javax.inject.Inject;
import javax.inject.Singleton;

import alektas.pocketbasket.domain.AppRepository;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

@Singleton
public class AppRepositoryImpl implements AppRepository {
    private AppPreferences mPrefs;
    private BehaviorSubject<Boolean> viewModeState;

    @Inject
    public AppRepositoryImpl(AppPreferences prefs) {
        mPrefs = prefs;
        viewModeState = BehaviorSubject.create();
        viewModeState.onNext(prefs.isShowcaseMode());
    }

    @Override
    public Observable<Boolean> observeViewMode() {
        return viewModeState;
    }

    @Override
    public void setViewMode(boolean isShowcaseMode) {
        viewModeState.onNext(isShowcaseMode);
        mPrefs.saveViewMode(isShowcaseMode);
    }

}