package userservice.mapper;

import org.springframework.stereotype.Component;
import userservice.dto.UserResponse;
import userservice.dto.UserUpdateRequest;
import userservice.entity.UserEntity;

import java.util.Locale;

/**
 * Преобразует данные между DTO и JPA-сущностью пользователя.
 */
@Component
public class UserMapper {

    /**
     * Создает сущность пользователя из уже подготовленных данных.
     *
     * @param name имя пользователя
     * @param email e-mail пользователя
     * @param age возраст пользователя
     * @return новая сущность пользователя
     */
    public UserEntity toEntity(String name, String email, Integer age) {
        return new UserEntity(
                normalizeName(name),
                normalizeEmail(email),
                age
        );
    }

    /**
     * Создает DTO ответа из сущности.
     *
     * @param userEntity сущность пользователя из базы
     * @return DTO с данными, которые можно вернуть клиенту
     */
    public UserResponse toResponse(UserEntity userEntity) {
        return new UserResponse(
                userEntity.getId(),
                userEntity.getName(),
                userEntity.getEmail(),
                userEntity.getAge(),
                userEntity.getCreatedAt()
        );
    }

    /**
     * Обновляет изменяемые поля сущности данными из request DTO.
     *
     * @param userEntity существующая сущность пользователя
     * @param request DTO с новыми данными
     */
    public void updateEntity(UserEntity userEntity, UserUpdateRequest request) {
        userEntity.setName(normalizeName(request.name()));
        userEntity.setEmail(normalizeEmail(request.email()));
        userEntity.setAge(request.age());
    }

    /**
     * Нормализует e-mail: убирает пробелы по краям и приводит к нижнему регистру.
     *
     * @param email исходный e-mail
     * @return нормализованный e-mail
     */
    public String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeName(String name) {
        return name.trim();
    }
}
