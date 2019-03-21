package alektas.pocketbasket.guide;

public interface GuideHelper {

    void onCaseHappened(String caseName);

    boolean isGuideStarted();

    String currentCase();

    void startGuide();

    void nextCase();

    void finishGuide();
}
