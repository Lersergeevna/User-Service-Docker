package userservice.service.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import userservice.event.UserNotificationEvent;
import userservice.event.UserOperation;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit-тесты Kafka publisher без запуска реального Kafka broker.
 */
@ExtendWith(MockitoExtension.class)
class KafkaNotificationEventPublisherTest {

    private static final String TOPIC = "user-notifications";
    private static final String EVENT_ID = "event-1";
    private static final String EMAIL = "alice@example.com";

    @Mock
    private KafkaTemplate<String, UserNotificationEvent> kafkaTemplate;

    private KafkaNotificationEventPublisher publisher;

    /**
     * Создает publisher с тестовым topic.
     */
    @BeforeEach
    void setUp() {
        publisher = new KafkaNotificationEventPublisher(kafkaTemplate, TOPIC);
    }

    /**
     * Проверяет, что publisher отправляет событие в нужный topic с e-mail как key.
     */
    @Test
    void publish_shouldSendEventToKafkaTopic() {
        UserNotificationEvent event = new UserNotificationEvent(EVENT_ID, UserOperation.CREATED, EMAIL);
        CompletableFuture<SendResult<String, UserNotificationEvent>> future = CompletableFuture.completedFuture(null);

        when(kafkaTemplate.send(eq(TOPIC), eq(EMAIL), eq(event))).thenReturn(future);

        publisher.publish(event);

        ArgumentCaptor<UserNotificationEvent> eventCaptor = ArgumentCaptor.forClass(UserNotificationEvent.class);

        verify(kafkaTemplate).send(
                eq(TOPIC),
                eq(EMAIL),
                eventCaptor.capture()
        );

        assertEquals(EVENT_ID, eventCaptor.getValue().eventId());
        assertEquals(UserOperation.CREATED, eventCaptor.getValue().operation());
        assertEquals(EMAIL, eventCaptor.getValue().email());
    }

    /**
     * Проверяет, что асинхронная ошибка Kafka не выбрасывается наружу.
     */
    @Test
    void publish_shouldNotThrowException_whenKafkaFutureFails() {
        UserNotificationEvent event = new UserNotificationEvent(EVENT_ID, UserOperation.CREATED, EMAIL);
        CompletableFuture<SendResult<String, UserNotificationEvent>> failedFuture = new CompletableFuture<>();

        when(kafkaTemplate.send(eq(TOPIC), eq(EMAIL), eq(event))).thenReturn(failedFuture);

        assertDoesNotThrow(() -> publisher.publish(event));

        failedFuture.completeExceptionally(new IllegalStateException("Kafka недоступна"));

        verify(kafkaTemplate).send(eq(TOPIC), eq(EMAIL), eq(event));
    }

    /**
     * Проверяет, что мгновенная ошибка KafkaTemplate тоже не выбрасывается наружу.
     */
    @Test
    void publish_shouldNotThrowException_whenKafkaTemplateThrowsImmediately() {
        UserNotificationEvent event = new UserNotificationEvent(EVENT_ID, UserOperation.CREATED, EMAIL);

        when(kafkaTemplate.send(eq(TOPIC), eq(EMAIL), eq(event)))
                .thenThrow(new IllegalStateException("Kafka producer не настроен"));

        assertDoesNotThrow(() -> publisher.publish(event));

        verify(kafkaTemplate).send(eq(TOPIC), eq(EMAIL), eq(event));
    }
}