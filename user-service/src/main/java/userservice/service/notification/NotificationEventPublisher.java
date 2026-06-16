package userservice.service.notification;

import userservice.event.UserNotificationEvent;

/**
 * Публикует события для сервиса уведомлений.
 */
public interface NotificationEventPublisher {

    /**
     * Отправляет событие о действии с пользователем.
     *
     * @param event событие для отправки
     */
    void publish(UserNotificationEvent event);
}
