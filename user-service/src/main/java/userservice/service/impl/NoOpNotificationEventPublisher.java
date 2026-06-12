package userservice.service.impl;

import userservice.event.UserNotificationEvent;
import userservice.service.NotificationEventPublisher;

/**
 * Publisher-заглушка для сценариев, где отправка Kafka-событий не нужна.
 */
public class NoOpNotificationEventPublisher implements NotificationEventPublisher {

    /**
     * Принимает событие, но не отправляет его во внешнюю систему.
     *
     * @param event событие уведомления
     */
    @Override
    public void publish(UserNotificationEvent event) {
        // Ничего не делает.
    }

    /**
     * Закрывает publisher-заглушку.
     * У заглушки нет внешних ресурсов, поэтому метод ничего не делает.
     */
    @Override
    public void close() {
        // Нет ресурсов для закрытия.
    }
}
