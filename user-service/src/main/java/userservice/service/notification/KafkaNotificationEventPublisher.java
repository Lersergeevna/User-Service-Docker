package userservice.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import userservice.constants.Messages;
import userservice.event.UserNotificationEvent;
import userservice.exception.NotificationEventPublishException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;

/**
 * Отправляет события уведомлений в Kafka topic.
 */
@Component
@RequiredArgsConstructor
public class KafkaNotificationEventPublisher implements NotificationEventPublisher {
    private static final long SEND_TIMEOUT_SECONDS = 5;

    private final KafkaTemplate<String, UserNotificationEvent> kafkaTemplate;

    @Value("${app.kafka.topics.user-notifications}")
    private String topic;

    /**
     * Публикует событие в Kafka. В качестве ключа используется e-mail пользователя,
     * чтобы события по одному адресу попадали в одну partition при масштабировании topic.
     *
     * @param event событие для отправки
     */
    @Override
    public void publish(UserNotificationEvent event) {
        try {
            kafkaTemplate.send(topic, event.email(), event).get(SEND_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new NotificationEventPublishException(Messages.NOTIFICATION_EVENT_PUBLISH_FAILED, e);
        } catch (ExecutionException | TimeoutException | RuntimeException e) {
            throw new NotificationEventPublishException(Messages.NOTIFICATION_EVENT_PUBLISH_FAILED, e);
        }
    }
}
