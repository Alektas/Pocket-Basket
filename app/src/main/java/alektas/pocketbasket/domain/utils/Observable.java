package alektas.pocketbasket.domain.utils;

public interface Observable<T> {
    T getValue();
    void setValue(T value);
    void observe(Observer<T> observer);
    void notifyObservers();
    void clearObservers();
}
