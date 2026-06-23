package userservice.service.notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import userservice.event.UserNotificationEvent;
import userservice.event.UserOperation;

import static org.mockito.Mockito.verify;

/**
 * Unit-тесты listener-а, который передает внутреннее событие в Kafka publisher.
 */
@ExtendWith(MockitoExtension.class)
class UserNotificationEventListenerTest {

    @Mock
    private NotificationEventPublisher notificationEventPublisher;

    @InjectMocks
    private UserNotificationEventListener listener;

    /**
     * Проверяет, что listener передает событие в publisher.
     */
    @Test
    void handle_shouldDelegateEventToNotificationPublisher() {
        UserNotificationEvent event = new UserNotificationEvent(
                "event-1",
                UserOperation.CREATED,
                "alice@example.com"
        );

        listener.handle(event);

        verify(notificationEventPublisher).publish(event);
    }
}