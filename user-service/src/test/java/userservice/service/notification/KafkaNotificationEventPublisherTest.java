package userservice.service.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;
import userservice.event.UserNotificationEvent;
import userservice.event.UserOperation;
import userservice.exception.NotificationEventPublishException;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit-тесты Kafka publisher без запуска реального Kafka broker.
 */
@ExtendWith(MockitoExtension.class)
class KafkaNotificationEventPublisherTest {

    private static final String TOPIC = "user-notifications";

    @Mock
    private KafkaTemplate<String, UserNotificationEvent> kafkaTemplate;

    private KafkaNotificationEventPublisher publisher;

    /**
     * Создает publisher и задает topic через test reflection, потому что в production он приходит из application.yml.
     */
    @BeforeEach
    void setUp() {
        publisher = new KafkaNotificationEventPublisher(kafkaTemplate);
        ReflectionTestUtils.setField(publisher, "topic", TOPIC);
    }

    /**
     * Проверяет, что publisher отправляет событие в нужный topic с e-mail как key.
     */
    @Test
    void publish_shouldSendEventToKafkaTopic() {
        UserNotificationEvent event = new UserNotificationEvent(UserOperation.CREATED, "alice@example.com");
        CompletableFuture<SendResult<String, UserNotificationEvent>> future = CompletableFuture.completedFuture(null);

        when(kafkaTemplate.send(eq(TOPIC), eq("alice@example.com"), eq(event))).thenReturn(future);

        publisher.publish(event);

        ArgumentCaptor<UserNotificationEvent> eventCaptor = ArgumentCaptor.forClass(UserNotificationEvent.class);

        verify(kafkaTemplate).send(
                eq(TOPIC),
                eq("alice@example.com"),
                eventCaptor.capture()
        );

        assertEquals(UserOperation.CREATED, eventCaptor.getValue().operation());
        assertEquals("alice@example.com", eventCaptor.getValue().email());
    }

    /**
     * Проверяет, что ошибка Kafka преобразуется в custom exception приложения.
     */
    @Test
    void publish_shouldThrowCustomException_whenKafkaFails() {
        UserNotificationEvent event = new UserNotificationEvent(UserOperation.CREATED, "alice@example.com");
        CompletableFuture<SendResult<String, UserNotificationEvent>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new IllegalStateException("Kafka недоступна"));

        when(kafkaTemplate.send(eq(TOPIC), eq("alice@example.com"), eq(event))).thenReturn(failedFuture);

        assertThrows(NotificationEventPublishException.class, () -> publisher.publish(event));
    }
}
