package userservice.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import userservice.event.UserNotificationEvent;

/**
 * Обрабатывает внутренние события user-service после успешного завершения транзакции.
 */
@Component
@RequiredArgsConstructor
public class UserNotificationEventListener {

    private final NotificationEventPublisher notificationEventPublisher;

    /**
     * Отправляет Kafka-событие только после успешного commit транзакции.
     *
     * @param event событие уведомления
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(UserNotificationEvent event) {
        notificationEventPublisher.publish(event);
    }
}