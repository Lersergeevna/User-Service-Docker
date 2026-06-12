package userservice.exception;

/**
 * Исключение, сигнализирующее о некорректном пользовательском вводе.
 */
public class InvalidInputException extends ServiceException {
    /**
     * Создаёт исключение с сообщением.
     *
     * @param message безопасное для пользователя сообщение
     */
    public InvalidInputException(String message) {
        super(message);
    }

    /**
     * Создаёт исключение с сообщением и исходной причиной.
     *
     * @param message безопасное для пользователя сообщение
     * @param cause исходная причина ошибки
     */
    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }
}