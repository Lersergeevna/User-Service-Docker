package notificationservice.service;

import lombok.RequiredArgsConstructor;
import notificationservice.constants.Messages;
import notificationservice.dto.NotificationRequest;
import notificationservice.dto.NotificationResponse;
import notificationservice.event.UserNotificationEvent;
import notificationservice.exception.InvalidNotificationEventException;
import notificationservice.mail.EmailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Бизнес-логика непосредственной отправки email-уведомлений.
 */
@Service
@RequiredArgsConstructor
public class EmailNotificationService {

    private final EmailSender emailSender;
    private final NotificationMessageFactory messageFactory;

    /**
     * Отправляет email по событию пользователя.
     *
     * @param event событие о создании или удалении пользователя
     * @throws InvalidNotificationEventException если событие пустое или содержит некорректные данные
     */
    public void sendNotification(UserNotificationEvent event) {
        validateEvent(event);

        NotificationMessageFactory.EmailMessage message = messageFactory.createMessage(event.operation());
        emailSender.send(event.email(), message.subject(), message.text());
    }

    /**
     * Отправляет email по ручному REST-запросу и возвращает DTO результата.
     * Ручной REST-запрос не сохраняется в таблицу retry-уведомлений.
     *
     * @param request REST-запрос на отправку уведомления
     * @return ответ с e-mail получателя и статусом отправки
     */
    public NotificationResponse sendNotification(NotificationRequest request) {
        UserNotificationEvent event = new UserNotificationEvent(
                UUID.randomUUID().toString(),
                request.operation(),
                request.email()
        );

        sendNotification(event);
        return new NotificationResponse(request.email(), Messages.STATUS_SENT);
    }

    /**
     * Проверяет корректность события перед отправкой письма.
     *
     * @param event проверяемое событие
     * @throws InvalidNotificationEventException если событие некорректно
     */
    private void validateEvent(UserNotificationEvent event) {
        if (event == null || event.operation() == null || event.email() == null || event.email().isBlank()) {
            throw new InvalidNotificationEventException(Messages.INVALID_NOTIFICATION_EVENT);
        }
    }
}
