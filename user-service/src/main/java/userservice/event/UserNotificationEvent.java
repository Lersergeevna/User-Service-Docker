package userservice.event;

/**
 * Kafka-событие для уведомлений о действиях с пользователем.
 *
 * @param operation тип операции с пользователем
 * @param email e-mail пользователя, которому нужно отправить уведомление
 */
public record UserNotificationEvent(
        String eventId,
        UserOperation operation,
        String email
) {
}