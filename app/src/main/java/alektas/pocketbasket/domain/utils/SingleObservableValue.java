package alektas.pocketbasket.domain.utils;

public class SingleObservableValue<T> implements Observable<T> {
    private Observer<T> mObserver;
    private T mValue;

    public SingleObservableValue() { }

    public SingleObservableValue(T value) {
        mValue = value;
    }

    @Override
    public void setValue(T value) {
        mValue = value;
        if (mObserver == null) return;
        notifyObservers();
    }

    @Override
    public T getValue() {
        return mValue;
    }

    @Override
    public void observe(Observer<T> observer) {
        if (observer == null) return;
        mObserver = observer;
        notifyObservers();
    }

    @Override
    public void notifyObservers() {
        mObserver.onChanged(mValue);
    }

    @Override
    public void clearObservers() {
        mObserver = null;
    }
}
