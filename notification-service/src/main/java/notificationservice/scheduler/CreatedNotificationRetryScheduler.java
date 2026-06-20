package notificationservice.scheduler;

import lombok.RequiredArgsConstructor;
import notificationservice.service.NotificationMessageService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Планировщик повторной отправки уведомлений о создании аккаунта.
 */
@Component
@RequiredArgsConstructor
public class CreatedNotificationRetryScheduler {

    private final NotificationMessageService notificationMessageService;

    /**
     * Запускает повторную отправку неотправленных CREATED-уведомлений по расписанию.
     */
    @Scheduled(fixedDelayString = "${app.scheduler.created-notification-retry-delay-ms}")
    public void retryCreatedNotifications() {
        notificationMessageService.retryUnsentCreatedNotifications();
    }
}
