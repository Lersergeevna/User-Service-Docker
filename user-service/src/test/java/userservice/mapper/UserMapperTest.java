package userservice.mapper;

import org.junit.jupiter.api.Test;
import userservice.dto.UserCreateRequest;
import userservice.dto.UserUpdateRequest;
import userservice.entity.UserEntity;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {
    @Test
    void toEntity_shouldCreateUserEntity_whenRequestIsValid() {
        UserCreateRequest request = new UserCreateRequest("Anna", "anna@mail.com", 25);

        UserEntity result = UserMapper.toEntity(request);

        assertAll(
                () -> assertEquals("Anna", result.getName()),
                () -> assertEquals("anna@mail.com", result.getEmail()),
                () -> assertEquals(25, result.getAge())
        );
    }

    @Test
    void applyUpdate_shouldUpdateUserEntity_whenRequestIsValid() {
        UserEntity userEntity = new UserEntity("Old Anna", "old.anna@mail.com", 20);
        UserUpdateRequest request = new UserUpdateRequest(1L, "New Anna", "new.anna@mail.com", 26);

        UserEntity result = UserMapper.applyUpdate(userEntity, request);

        assertAll(
                () -> assertEquals("New Anna", result.getName()),
                () -> assertEquals("new.anna@mail.com", result.getEmail()),
                () -> assertEquals(26, result.getAge())
        );
    }
}