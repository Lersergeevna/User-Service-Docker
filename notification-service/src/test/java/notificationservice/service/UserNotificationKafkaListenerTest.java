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
 * Unit-тест Kafka listener без запуска Kafka broker.
 */
@ExtendWith(MockitoExtension.class)
class UserNotificationKafkaListenerTest {

    @Mock
    private NotificationMessageService notificationMessageService;

    @InjectMocks
    private UserNotificationKafkaListener listener;

    /**
     * Проверяет, что listener передает полученное событие в сервис обработки уведомлений.
     */
    @Test
    void listen_shouldDelegateEventToNotificationMessageService() {
        UserNotificationEvent event = new UserNotificationEvent(UserOperation.CREATED, "alice@example.com");

        listener.listen(event);

        verify(notificationMessageService).processKafkaEvent(event);
    }
}
