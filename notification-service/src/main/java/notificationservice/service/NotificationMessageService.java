package notificationservice.service;

import lombok.RequiredArgsConstructor;
import notificationservice.constants.Messages;
import notificationservice.entity.NotificationMessageEntity;
import notificationservice.entity.NotificationStatus;
import notificationservice.event.UserNotificationEvent;
import notificationservice.event.UserOperation;
import notificationservice.exception.InvalidNotificationEventException;
import notificationservice.repository.NotificationMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Управляет уведомлениями, которые требуют хранения статуса отправки.
 */
@Service
@RequiredArgsConstructor
public class NotificationMessageService {
    private static final int LAST_ERROR_MAX_LENGTH = 1000;

    private final NotificationMessageRepository notificationMessageRepository;
    private final EmailNotificationService emailNotificationService;

    /**
     * Обрабатывает событие из Kafka.
     * Повторное событие с уже успешно обработанным eventId не отправляет письмо повторно.
     *
     * @param event событие пользователя
     */
    @Transactional
    public void processKafkaEvent(UserNotificationEvent event) {
        validateEvent(event);

        NotificationMessageEntity message = notificationMessageRepository.findByEventId(event.eventId())
                .orElseGet(() -> createPendingMessage(event));

        if (message.getStatus() == NotificationStatus.SENT) {
            return;
        }

        trySendMessage(message);
    }

    /**
     * Повторно отправляет неотправленные уведомления о создании аккаунта.
     */
    @Transactional
    public void retryUnsentCreatedNotifications() {
        List<NotificationMessageEntity> messages = notificationMessageRepository
                .findTop50ByOperationAndStatusInOrderByCreatedAtAsc(
                        UserOperation.CREATED,
                        List.of(NotificationStatus.PENDING, NotificationStatus.FAILED)
                );

        messages.forEach(this::trySendMessage);
    }

    private NotificationMessageEntity createPendingMessage(UserNotificationEvent event) {
        NotificationMessageEntity message = new NotificationMessageEntity(
                event.eventId(),
                event.email(),
                event.operation(),
                NotificationStatus.PENDING
        );

        return notificationMessageRepository.save(message);
    }

    private void trySendMessage(NotificationMessageEntity message) {
        try {
            emailNotificationService.sendNotification(
                    new UserNotificationEvent(
                            message.getEventId(),
                            message.getOperation(),
                            message.getEmail()
                    )
            );
            message.markSent();
        } catch (RuntimeException e) {
            message.markFailed(cutErrorMessage(e.getMessage()));
        }

        notificationMessageRepository.save(message);
    }

    private void validateEvent(UserNotificationEvent event) {
        if (event == null
                || event.eventId() == null
                || event.eventId().isBlank()
                || event.operation() == null
                || event.email() == null
                || event.email().isBlank()) {
            throw new InvalidNotificationEventException(Messages.INVALID_NOTIFICATION_EVENT);
        }
    }

    private String cutErrorMessage(String message) {
        if (message == null || message.isBlank()) {
            return Messages.EMAIL_SEND_FAILED;
        }

        if (message.length() <= LAST_ERROR_MAX_LENGTH) {
            return message;
        }

        return message.substring(0, LAST_ERROR_MAX_LENGTH);
    }
}