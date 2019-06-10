package alektas.pocketbasket.domain.utils;

/**
 * Disposable event that returns containing value only once.
 * After the first requesting returns 'null'.
 *
 * @param <T> entity that this event contain
 */
public class Event<T> {
    private T mValue;
    private boolean isHandled;

    public Event(T value) {
        mValue = value;
    }

    /**
     * Disposable request for the entity that is in the Event.
     *
     * @return entity at the first request or null after.
     */
    public T getValue() {
        if (isHandled) return null;
        isHandled = true;
        return mValue;
    }
}
