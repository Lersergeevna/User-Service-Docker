package userservice.service;

import userservice.event.UserNotificationEvent;

/**
 * Публикует события уведомлений о пользователях.
 */
public interface NotificationEventPublisher extends AutoCloseable {

    /**
     * Отправляет событие уведомления.
     *
     * @param event событие уведомления
     */
    void publish(UserNotificationEvent event);

    /**
     * Закрывает ресурсы publisher.
     */
    @Override
    void close();
}