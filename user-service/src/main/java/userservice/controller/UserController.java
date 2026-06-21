package userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
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
import userservice.assembler.UserModelAssembler;
import userservice.constants.UserApiDoc;
import userservice.dto.UserCreateRequest;
import userservice.dto.UserResponse;
import userservice.dto.UserUpdateRequest;
import userservice.service.UserService;

import java.util.List;

/**
 * REST-контроллер для CRUD-операций с пользователями.
 */
@Tag(name = UserApiDoc.TAG_NAME, description = UserApiDoc.TAG_DESCRIPTION)
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserModelAssembler userModelAssembler;

    /**
     * Создает пользователя.
     *
     * @param request валидные данные нового пользователя
     * @return созданный пользователь в формате HATEOAS-модели
     */
    @Operation(
            summary = UserApiDoc.CREATE_USER_SUMMARY,
            description = UserApiDoc.CREATE_USER_DESCRIPTION
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = UserApiDoc.USER_CREATED),
            @ApiResponse(responseCode = "400", description = UserApiDoc.BAD_REQUEST),
            @ApiResponse(responseCode = "409", description = UserApiDoc.EMAIL_CONFLICT)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserResponse createdUser = userService.createUser(request);
        return userModelAssembler.toModel(createdUser);
    }

    /**
     * Возвращает пользователя по id.
     *
     * @param id идентификатор пользователя из URL
     * @return найденный пользователь в формате HATEOAS-модели
     */
    @Operation(
            summary = UserApiDoc.GET_USER_BY_ID_SUMMARY,
            description = UserApiDoc.GET_USER_BY_ID_DESCRIPTION
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = UserApiDoc.USER_FOUND),
            @ApiResponse(responseCode = "404", description = UserApiDoc.USER_NOT_FOUND)
    })
    @GetMapping("/{id}")
    public EntityModel<UserResponse> getUserById(
            @Parameter(description = UserApiDoc.USER_ID, example = "1")
            @PathVariable long id
    ) {
        UserResponse user = userService.getUserById(id);
        return userModelAssembler.toModel(user);
    }

    /**
     * Возвращает всех пользователей.
     *
     * @return список пользователей в формате HATEOAS-коллекции
     */
    @Operation(
            summary = UserApiDoc.GET_ALL_USERS,
            description = UserApiDoc.RETURN_ALL_USERS_LIST
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = UserApiDoc.USERS_LIST_RETURNED)
    })
    @GetMapping
    public CollectionModel<EntityModel<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return userModelAssembler.toCollectionModel(users);
    }

    /**
     * Обновляет пользователя по id.
     *
     * @param id идентификатор пользователя из URL
     * @param request валидные новые данные пользователя
     * @return обновленный пользователь в формате HATEOAS-модели
     */
    @Operation(
            summary = UserApiDoc.UPDATE_USER_SUMMARY,
            description = UserApiDoc.UPDATE_USER_DESCRIPTION
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = UserApiDoc.USER_UPDATED),
            @ApiResponse(responseCode = "400", description = UserApiDoc.BAD_REQUEST),
            @ApiResponse(responseCode = "404", description = UserApiDoc.USER_NOT_FOUND),
            @ApiResponse(responseCode = "409", description = UserApiDoc.EMAIL_CONFLICT)
    })
    @PutMapping("/{id}")
    public EntityModel<UserResponse> updateUser(
            @Parameter(description = UserApiDoc.USER_ID, example = "1")
            @PathVariable long id,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        UserResponse updatedUser = userService.updateUser(id, request);
        return userModelAssembler.toModel(updatedUser);
    }

    /**
     * Удаляет пользователя по id.
     *
     * @param id идентификатор пользователя из URL
     */
    @Operation(
            summary = UserApiDoc.DELETE_USER_SUMMARY,
            description = UserApiDoc.DELETE_USER_DESCRIPTION
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = UserApiDoc.USER_DELETED),
            @ApiResponse(responseCode = "404", description = UserApiDoc.USER_NOT_FOUND)
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(
            @Parameter(description = UserApiDoc.USER_ID, example = "1")
            @PathVariable long id
    ) {
        userService.deleteUser(id);
    }
}