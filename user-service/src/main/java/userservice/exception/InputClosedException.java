package userservice.exception;

/**
 * Пользователь закрыл поток ввода консоли.
 */
public class InputClosedException extends RuntimeException {
    public InputClosedException(String message) {
        super(message);
    }
}
