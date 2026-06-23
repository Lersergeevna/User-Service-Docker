package notificationservice.event;

/**
 * Сообщение о пользовательской операции.
 *
 * @param operation операция над пользователем
 * @param email e-mail получателя
 */
public record UserNotificationEvent(
        String eventId,
        UserOperation operation,
        String email
) {
}
