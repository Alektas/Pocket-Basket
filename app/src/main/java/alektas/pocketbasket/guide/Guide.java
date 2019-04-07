package alektas.pocketbasket.guide;

public interface Guide {

    /**
     * Inform the guide that the case has happened
     * @param caseKey name of the case that is happened
     */
    void onCaseHappened(String caseKey);

    Guide addCase(GuideCase guideCase);

    String currentCaseKey();

    int caseNumb(String caseKey);

    boolean isGuideStarted();

    void startGuide();

    void nextCase();

    void startFrom(String caseKey);

    void finishGuide();

}
