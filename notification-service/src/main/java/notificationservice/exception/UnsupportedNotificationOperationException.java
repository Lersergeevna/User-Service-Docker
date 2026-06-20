package notificationservice.exception;

/**
 * Исключение для неподдерживаемой операции уведомления.
 */
public class UnsupportedNotificationOperationException extends NotificationServiceException {

    public UnsupportedNotificationOperationException(String message) {
        super(message);
    }
}
