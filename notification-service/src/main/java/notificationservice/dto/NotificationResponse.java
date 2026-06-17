package notificationservice.dto;

/**
 * Ответ после ручной отправки уведомления.
 *
 * @param email адрес получателя
 * @param status статус отправки
 */
public record NotificationResponse(
        String email,
        String status
) {
}
