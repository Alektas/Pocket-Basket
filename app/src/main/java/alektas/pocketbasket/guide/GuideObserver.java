package alektas.pocketbasket.guide;

import alektas.pocketbasket.guide.ui.GuidePresenter;

public interface GuideObserver {
    /**
     * There should be applied view states that appropriate only for the start of the guide.
     * These states can be changed during the guide.
     * View states that don't change during the guide should be applied in the callbacks
     * of the guide state observer. In this way they are applied at each device rotation.
     * Triggered <b>after</b> the guide is started.
     */
    void onGuideStart();

    /**
     * There should be applied view states that appropriate only for the end of the guide.
     * These states can be changed during the guide.
     * View states that don't change during the guide should be applied in the callbacks
     * of the guide state observer. In this way they are applied at each device rotation.
     * Triggered <b>after</b> the guide is finished.
     */
    void onGuideFinish();

    /**
     * Triggered <b>after</b> the guide case with key <i>caseKey</i> is started.
     * There a guide case state should be applied in the view model or guide case view should
     * be shown. For example by {@link GuidePresenter#showCase(String)} in the presentation layer.
     *
     * @param caseKey key of the guide case
     */
    void onGuideCaseStart(String caseKey);

    /**
     * Triggered <b>after</b> the guide case with key {@code caseKey} is finished.
     *
     * @param caseKey key of the guide case
     */
    void onGuideCaseComplete(String caseKey);
}