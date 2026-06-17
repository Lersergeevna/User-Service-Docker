package notificationservice.exception;

/**
 * Исключение для ошибок SMTP-отправки письма.
 */
public class EmailSendingException extends NotificationServiceException {

    public EmailSendingException(String message, Throwable cause) {
        super(message, cause);
    }
}
