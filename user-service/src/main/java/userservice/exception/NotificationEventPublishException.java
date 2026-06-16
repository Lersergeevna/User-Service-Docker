package userservice.exception;

/**
 * Исключение выбрасывается, если user-service не смог отправить событие уведомления в Kafka.
 */
public class NotificationEventPublishException extends RuntimeException {

    /**
     * Создаёт исключение с сообщением и исходной причиной ошибки.
     *
     * @param message сообщение об ошибке
     * @param cause исходная причина ошибки
     */
    public NotificationEventPublishException(String message, Throwable cause) {
        super(message, cause);
    }
}