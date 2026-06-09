package userservice.exception;

/**
 * Исключение для ситуации, когда e-mail уже занят другим пользователем.
 */
public class DuplicateEmailException extends RuntimeException {

    /**
     * Создает исключение с сообщением для клиента.
     *
     * @param message описание ошибки
     */
    public DuplicateEmailException(String message) {
        super(message);
    }
}
