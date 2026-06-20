package userservice.service.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import userservice.constants.Messages;
import userservice.event.UserNotificationEvent;

/**
 * Отправляет события уведомлений в Kafka topic.
 */
@Slf4j
@Component
public class KafkaNotificationEventPublisher implements NotificationEventPublisher {

    private final KafkaTemplate<String, UserNotificationEvent> kafkaTemplate;
    private final String topic;

    public KafkaNotificationEventPublisher(
            KafkaTemplate<String, UserNotificationEvent> kafkaTemplate,
            @Value("${app.kafka.topics.user-notifications}") String topic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    /**
     * Публикует событие в Kafka асинхронно.
     * В качестве ключа используется e-mail пользователя.
     *
     * @param event событие уведомления
     */
    @Override
    public void publish(UserNotificationEvent event) {
        try {
            kafkaTemplate.send(topic, event.email(), event)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            log.error(Messages.NOTIFICATION_EVENT_PUBLISH_FAILED, event.email(), exception);
                            return;
                        }

                        log.info(Messages.NOTIFICATION_EVENT_PUBLISHED, event.email());
                    });
        } catch (RuntimeException exception) {
            log.error(Messages.NOTIFICATION_EVENT_PUBLISH_FAILED, event.email(), exception);
        }
    }
}