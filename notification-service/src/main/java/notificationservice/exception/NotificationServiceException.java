package notificationservice.exception;

/**
 * Базовое исключение notification-service для ошибок приложения.
 */
public abstract class NotificationServiceException extends RuntimeException {

    protected NotificationServiceException(String message) {
        super(message);
    }

    protected NotificationServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
