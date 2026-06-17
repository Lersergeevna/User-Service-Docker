package notificationservice.service;

import lombok.RequiredArgsConstructor;
import notificationservice.event.UserNotificationEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Получает события user-service из Kafka и передает их в сервис обработки уведомлений.
 */
@Component
@RequiredArgsConstructor
public class UserNotificationKafkaListener {

    private final NotificationMessageService notificationMessageService;

    /**
     * Обрабатывает событие из Kafka topic user-notifications.
     *
     * @param event событие о создании или удалении пользователя
     */
    @KafkaListener(
            topics = "${app.kafka.topics.user-notifications}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listen(UserNotificationEvent event) {
        notificationMessageService.processKafkaEvent(event);
    }
}
