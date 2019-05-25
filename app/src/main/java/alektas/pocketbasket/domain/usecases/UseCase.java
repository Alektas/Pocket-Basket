package alektas.pocketbasket.domain.usecases;

public interface UseCase<REQUEST, RESPONSE> {

    void execute(REQUEST request, Callback<RESPONSE> callback);

    interface Callback<R> {
        void onResponse(R response);
    }
}
