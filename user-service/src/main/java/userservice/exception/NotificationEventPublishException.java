package userservice.exception;

/**
 * Исключение для ошибок отправки события уведомления в Kafka.
 */
public class NotificationEventPublishException extends ServiceException {

    public NotificationEventPublishException(String message, Throwable cause) {
        super(message, cause);
    }
}