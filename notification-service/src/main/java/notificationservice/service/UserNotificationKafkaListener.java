package notificationservice.service;

import lombok.RequiredArgsConstructor;
import notificationservice.event.UserNotificationEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Получает события user-service из Kafka и запускает отправку письма.
 */
@Component
@RequiredArgsConstructor
public class UserNotificationKafkaListener {

    private final EmailNotificationService emailNotificationService;

    @KafkaListener(
            topics = "${app.kafka.topics.user-notifications}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listen(UserNotificationEvent event) {
        emailNotificationService.sendNotification(event);
    }
}
