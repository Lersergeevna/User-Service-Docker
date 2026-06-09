package userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import userservice.constants.Messages;

/**
 * DTO для запроса на создание пользователя.
 */
public record UserCreateRequest(
        @NotBlank(message = Messages.INVALID_NAME)
        @Size(max = 100, message = Messages.INVALID_NAME)
        String name,

        @NotBlank(message = Messages.INVALID_EMAIL)
        @Email(message = Messages.INVALID_EMAIL)
        @Size(max = 150, message = Messages.INVALID_EMAIL_LENGTH)
        String email,

        @NotNull(message = Messages.INVALID_AGE)
        @Min(value = 1, message = Messages.INVALID_AGE)
        @Max(value = 130, message = Messages.INVALID_AGE)
        Integer age
) {
}
