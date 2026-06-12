package userservice.dto;

// Неизменяемый запрос на создание пользователя.
public record UserCreateRequest(String name, String email, int age) {
}