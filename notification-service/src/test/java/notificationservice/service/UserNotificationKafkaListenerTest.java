package notificationservice.service;

import notificationservice.event.UserNotificationEvent;
import notificationservice.event.UserOperation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

/**
 * Unit-тесты Kafka listener-а.
 */
@ExtendWith(MockitoExtension.class)
class UserNotificationKafkaListenerTest {

    @Mock
    private NotificationMessageService notificationMessageService;

    @InjectMocks
    private UserNotificationKafkaListener listener;

    @Test
    void listen_shouldDelegateEventToNotificationMessageService() {
        UserNotificationEvent event = new UserNotificationEvent(
                "event-listener-1",
                UserOperation.CREATED,
                "alice@example.com"
        );

        listener.listen(event);

        verify(notificationMessageService).processKafkaEvent(event);
    }
}