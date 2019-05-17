package alektas.pocketbasket.domain.utils;

import java.util.ArrayList;
import java.util.List;

public class MultiObservableValue<T> implements Observable<T> {
    private List<Observer<T>> mObservers;
    private T mValue;

    public MultiObservableValue() {
        mObservers = new ArrayList<>();
    }

    public MultiObservableValue(T value) {
        this();
        mValue = value;
    }

    @Override
    public void setValue(T value) {
        mValue = value;
        if (mObservers.isEmpty()) return;
        notifyObservers();
    }

    @Override
    public T getValue() {
        return mValue;
    }

    @Override
    public void observe(Observer<T> observer) {
        if (observer == null) return;
        mObservers.add(observer);
        notifyObservers();
    }

    @Override
    public void clearObservers() {
        mObservers.clear();
    }

    private void notifyObservers() {
        for (Observer<T> observer : mObservers) {
            observer.onChanged(mValue);
        }
    }
}
