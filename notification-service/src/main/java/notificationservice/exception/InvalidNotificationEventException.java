package notificationservice.exception;

/**
 * Исключение для некорректного события уведомления.
 */
public class InvalidNotificationEventException extends NotificationServiceException {

    public InvalidNotificationEventException(String message) {
        super(message);
    }
}
