package userservice.dto;

// Неизменяемый запрос на обновление пользователя.
public record UserUpdateRequest(long id, String name, String email, int age) {
}