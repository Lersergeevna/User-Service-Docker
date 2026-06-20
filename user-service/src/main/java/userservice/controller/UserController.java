package userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import userservice.dto.UserCreateRequest;
import userservice.dto.UserResponse;
import userservice.dto.UserUpdateRequest;
import userservice.service.UserService;

import java.util.List;

/**
 * REST-контроллер для CRUD-операций с пользователями.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * Создает пользователя.
     *
     * @param request валидные данные нового пользователя
     * @return созданный пользователь в формате DTO ответа
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody UserCreateRequest request) {
        return userService.createUser(request);
    }

    /**
     * Возвращает пользователя по id.
     *
     * @param id идентификатор пользователя из URL
     * @return найденный пользователь в формате DTO ответа
     */
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable long id) {
        return userService.getUserById(id);
    }

    /**
     * Возвращает всех пользователей.
     *
     * @return список пользователей в формате DTO ответа
     */
    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Обновляет пользователя по id.
     *
     * @param id идентификатор пользователя из URL
     * @param request валидные новые данные пользователя
     * @return обновленный пользователь в формате DTO ответа
     */
    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable long id, @Valid @RequestBody UserUpdateRequest request) {
        return userService.updateUser(id, request);
    }

    /**
     * Удаляет пользователя по id.
     *
     * @param id идентификатор пользователя из URL
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
    }
}
