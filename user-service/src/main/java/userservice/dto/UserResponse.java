package userservice.dto;

import java.time.LocalDateTime;

/**
 * DTO пользователя для внешнего REST API.
 *
 * @param id идентификатор пользователя
 * @param name имя пользователя
 * @param maskedEmail замаскированный email пользователя
 * @param age возраст пользователя
 * @param createdAt дата создания пользователя
 */
public record UserResponse(
        Long id,
        String name,
        String maskedEmail,
        Integer age,
        LocalDateTime createdAt
) {
}