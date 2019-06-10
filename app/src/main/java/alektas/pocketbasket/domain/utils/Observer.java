package alektas.pocketbasket.domain.utils;

/**
 * A simple callback that can receive from {@link }.
 *
 * @param <T> The type of the parameter
 */
public interface Observer<T> {
    /**
     * Called when the data is changed.
     * @param t  The new data
     */
    void onChanged(T t);
}