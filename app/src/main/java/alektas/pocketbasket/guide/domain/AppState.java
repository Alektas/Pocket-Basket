package alektas.pocketbasket.guide.domain;

import java.util.ArrayList;
import java.util.List;

public class AppState<T> {
    private String mKey;
    private T mState;
    private List<StateObserver<T>> mObservers;

    public interface StateObserver<T> {
        void onStateChange(T state);
    }

    public AppState(String key, T state) {
        mKey = key;
        mState = state;
        mObservers = new ArrayList<>();
    }

    public void observe(StateObserver<T> observer) {
        mObservers.add(observer);
        observer.onStateChange(mState);
    }

    public void notifyObservers() {
        for (StateObserver<T> observer : mObservers) {
            observer.onStateChange(mState);
        }
    }

    public void removeObserver(StateObserver<T> observer) {
        mObservers.remove(observer);
    }

    public void clearObservers() {
        mObservers = null;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public T getState() {
        return mState;
    }

    public void setState(T state) {
        mState = state;
        notifyObservers();
    }
}
