package alektas.pocketbasket.guide.ui;

public interface GuidePresenter {

    GuidePresenter addCase(GuideCaseView guideCase);

    void showCase(String key);

    void hideCase(String key);

    void hideCurrentCase();

}
