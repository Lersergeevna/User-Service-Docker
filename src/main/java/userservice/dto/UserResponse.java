package userservice.dto;

import java.time.LocalDateTime;

/**
 * DTO ответа с данными пользователя, которые можно отдавать клиенту.
 */
public record UserResponse(
        Long id,
        String name,
        String email,
        Integer age,
        LocalDateTime createdAt
) {
}
