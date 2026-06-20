package userservice.service;

import userservice.dto.UserCreateRequest;
import userservice.dto.UserResponse;
import userservice.dto.UserUpdateRequest;

import java.util.List;

/**
 * Описывает бизнес-операции для управления пользователями.
 */
public interface UserService {

    /**
     * Создает нового пользователя.
     *
     * @param request данные для создания пользователя
     * @return созданный пользователь в формате DTO ответа
     */
    UserResponse createUser(UserCreateRequest request);

    /**
     * Возвращает пользователя по id.
     *
     * @param id идентификатор пользователя
     * @return найденный пользователь в формате DTO ответа
     */
    UserResponse getUserById(long id);

    /**
     * Возвращает всех пользователей.
     *
     * @return список пользователей в формате DTO ответа
     */
    List<UserResponse> getAllUsers();

    /**
     * Обновляет данные пользователя.
     *
     * @param id идентификатор пользователя
     * @param request новые данные пользователя
     * @return обновленный пользователь в формате DTO ответа
     */
    UserResponse updateUser(long id, UserUpdateRequest request);

    /**
     * Удаляет пользователя по id.
     *
     * @param id идентификатор пользователя
     */
    void deleteUser(long id);
}
