package userservice.assembler;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;
import userservice.controller.UserController;
import userservice.dto.UserResponse;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * Собирает HATEOAS-модели пользователей со ссылками для навигации.
 */
@Component
public class UserModelAssembler {

    /**
     * Преобразует DTO пользователя в HATEOAS-модель.
     *
     * @param user DTO пользователя
     * @return модель пользователя со ссылками
     */
    public EntityModel<UserResponse> toModel(UserResponse user) {
        return EntityModel.of(
                user,
                linkTo(UserController.class).slash(user.id()).withSelfRel(),
                linkTo(UserController.class).withRel("users"),
                linkTo(UserController.class).slash(user.id()).withRel("update-user"),
                linkTo(UserController.class).slash(user.id()).withRel("delete-user")
        );
    }

    /**
     * Преобразует список пользователей в HATEOAS-коллекцию.
     *
     * @param users список DTO пользователей
     * @return коллекция пользователей со ссылками
     */
    public CollectionModel<EntityModel<UserResponse>> toCollectionModel(List<UserResponse> users) {
        List<EntityModel<UserResponse>> userModels = users.stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(
                userModels,
                linkTo(UserController.class).withSelfRel(),
                linkTo(UserController.class).withRel("create-user")
        );
    }
}