package alektas.pocketbasket.domain.usecases;

public interface UseCase<REQUEST, RESPONSE> {
    RESPONSE execute(REQUEST request);
}
