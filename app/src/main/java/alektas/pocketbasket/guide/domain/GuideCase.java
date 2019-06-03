package alektas.pocketbasket.guide.domain;

public interface GuideCase {
    String getKey();
    void linkCase(GuideCase guideCase);
    GuideCase getLinkedCase();
    void complete();
    boolean isCompleted();
}
