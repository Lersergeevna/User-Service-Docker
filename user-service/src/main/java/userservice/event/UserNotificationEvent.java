package userservice.event;

/**
 * Событие для notification-service.
 *
 * @param operation операция над пользователем
 * @param email e-mail пользователя
 */
public record UserNotificationEvent(
        UserOperation operation,
        String email
) {
}