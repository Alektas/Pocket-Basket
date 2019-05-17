package alektas.pocketbasket.domain.utils;

public interface Observable<T> {
    void setValue(T value);
    T getValue();
    void observe(Observer<T> observer);
    void clearObservers();
}
