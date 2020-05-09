package alektas.pocketbasket.domain;

import io.reactivex.Observable;

public interface AppRepository {

    /**
     * Contains current mode state.
     * 'true' = showcase mode, 'false' = basket mode.
     */
    Observable<Boolean> observeViewMode();
    void setViewMode(boolean showcaseMode);

}
