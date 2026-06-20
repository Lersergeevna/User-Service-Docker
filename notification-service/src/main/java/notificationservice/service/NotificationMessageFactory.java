package notificationservice.service;

import notificationservice.constants.Messages;
import notificationservice.event.UserOperation;
import notificationservice.exception.UnsupportedNotificationOperationException;
import org.springframework.stereotype.Component;

/**
 * Формирует тему и текст письма в зависимости от операции.
 */
@Component
public class NotificationMessageFactory {

    public EmailMessage createMessage(UserOperation operation) {
        if (operation == null) {
            throw new UnsupportedNotificationOperationException(Messages.UNSUPPORTED_NOTIFICATION_OPERATION);
        }

        return switch (operation) {
            case CREATED -> new EmailMessage(Messages.ACCOUNT_CREATED_SUBJECT, Messages.ACCOUNT_CREATED_TEXT);
            case DELETED -> new EmailMessage(Messages.ACCOUNT_DELETED_SUBJECT, Messages.ACCOUNT_DELETED_TEXT);
        };
    }

    public record EmailMessage(String subject, String text) {
    }
}
