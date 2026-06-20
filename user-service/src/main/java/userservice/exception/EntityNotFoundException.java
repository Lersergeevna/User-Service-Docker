package userservice.exception;

/**
 * Исключение для ситуации, когда запрошенная сущность не найдена.
 */
public class EntityNotFoundException extends RuntimeException {

    /**
     * Создает исключение с сообщением для клиента.
     *
     * @param message описание ошибки
     */
    public EntityNotFoundException(String message) {
        super(message);
    }
}
