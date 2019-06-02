package alektas.pocketbasket.guide.domain;

public abstract class Requirement implements AppState.StateObserver {
    private RequirementObserver mObserver;

    public interface RequirementObserver {
        void onChange(boolean isMet);
    }

    private Requirement() {}

    public Requirement(AppState... states) {
        for (AppState state : states) {
            state.observe(this);
        }
    }

    public void observe(RequirementObserver observer) {
        mObserver = observer;
        notifyObserver();
    }

    private void notifyObserver() {
        if (mObserver == null) return;
        mObserver.onChange(check());
    }

    public void removeObserver() {
        mObserver = null;
    }

    public abstract boolean check();

    @Override
    public void onStateChange(Object state) {
        notifyObserver();
    }
}
