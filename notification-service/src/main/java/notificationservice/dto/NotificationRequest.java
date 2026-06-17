package notificationservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import notificationservice.constants.Messages;
import notificationservice.event.UserOperation;

/**
 * Запрос ручной отправки уведомления через REST API.
 */
public record NotificationRequest(
        @NotNull(message = Messages.OPERATION_REQUIRED)
        UserOperation operation,

        @NotBlank(message = Messages.EMAIL_REQUIRED)
        @Email(message = Messages.INVALID_EMAIL)
        String email
) {
}
