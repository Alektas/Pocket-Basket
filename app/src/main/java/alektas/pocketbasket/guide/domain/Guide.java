package alektas.pocketbasket.guide.domain;

import alektas.pocketbasket.guide.GuideObserver;

public interface Guide {

    Guide addCase(GuideCase guideCase);

    /**
     * Inform the guide that the case has happened
     * @param caseKey name of the case that is happened
     */
    void onUserEvent(String caseKey);

    void start();

    void startFrom(String caseKey);

    void finish();

    boolean isStarted();

    String currentCase();

    void observe(GuideObserver listener);

    void removeObserver(GuideObserver listener);

}
