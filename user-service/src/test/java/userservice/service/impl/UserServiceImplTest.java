package userservice.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import userservice.dao.UserDao;
import userservice.dto.UserCreateRequest;
import userservice.dto.UserUpdateRequest;
import userservice.entity.BaseEntity;
import userservice.entity.UserEntity;
import userservice.exception.DataAccessException;
import userservice.exception.EntityNotFoundException;
import userservice.exception.InvalidInputException;
import userservice.exception.ServiceException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_shouldReturnUserId_whenRequestIsValid() {
        UserCreateRequest request = new UserCreateRequest("Anna", "anna@mail.com", 25);
        when(userDao.findByEmail("anna@mail.com"))
                .thenReturn(Optional.empty());
        when(userDao.save(any(UserEntity.class)))
                .thenReturn(1L);

        Long result = userService.createUser(request);

        assertEquals(1L, result);
        verify(userDao).findByEmail("anna@mail.com");
        verify(userDao).save(any(UserEntity.class));
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void createUser_shouldThrowException_whenEmailAlreadyExists() {
        UserCreateRequest request = new UserCreateRequest("Anna", "anna@mail.com", 25);
        UserEntity existingUser = new UserEntity("Old Anna", "anna@mail.com", 30);

        when(userDao.findByEmail("anna@mail.com"))
                .thenReturn(Optional.of(existingUser));

        assertThrows(InvalidInputException.class, () -> userService.createUser(request));

        verify(userDao).findByEmail("anna@mail.com");
        verify(userDao, never()).save(any(UserEntity.class));
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void createUser_shouldThrowException_whenDaoSaveFails() {
        UserCreateRequest request = new UserCreateRequest("Anna", "anna@mail.com", 25);

        when(userDao.findByEmail("anna@mail.com"))
                .thenReturn(Optional.empty());

        when(userDao.save(any(UserEntity.class)))
                .thenThrow(new DataAccessException("DB error", new RuntimeException()));

        assertThrows(ServiceException.class, () -> userService.createUser(request));

        verify(userDao).findByEmail("anna@mail.com");
        verify(userDao).save(any(UserEntity.class));
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void getUserById_shouldReturnUser_whenUserExists() {
        UserEntity user = new UserEntity("Anna", "anna@mail.com", 25);

        when(userDao.findById(1L))
                .thenReturn(Optional.of(user));

        UserEntity result = userService.getUserById(1L);

        assertEquals(user, result);

        verify(userDao).findById(1L);
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void getUserById_shouldThrowException_whenUserDoesNotExist() {
        when(userDao.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(999L));

        verify(userDao).findById(999L);
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void getUserById_shouldThrowServiceException_whenDaoFails() {
        when(userDao.findById(1L))
                .thenThrow(new DataAccessException("DB error", new RuntimeException()));

        assertThrows(ServiceException.class, () -> userService.getUserById(1L));

        verify(userDao).findById(1L);
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void getAllUsers_shouldReturnUsers_whenDaoReturnsUsers() {
        List<UserEntity> users = List.of(
                new UserEntity("Anna", "anna@mail.com", 25),
                new UserEntity("Bob", "bob@mail.com", 30)
        );

        when(userDao.findAll())
                .thenReturn(users);

        List<UserEntity> result = userService.getAllUsers();

        assertEquals(users, result);
        verify(userDao).findAll();
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void getAllUsers_shouldThrowServiceException_whenDaoFails() {
        when(userDao.findAll())
                .thenThrow(new DataAccessException("DB error", new RuntimeException()));

        assertThrows(ServiceException.class, () -> userService.getAllUsers());

        verify(userDao).findAll();
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void updateUser_shouldReturnUpdatedUser_whenRequestIsValid() {
        UserUpdateRequest request = new UserUpdateRequest(1L, "New Anna", "new.anna@mail.com", 26);
        UserEntity existingUser = new UserEntity("Anna", "anna@mail.com", 25);
        UserEntity updatedUser = new UserEntity("New Anna", "new.anna@mail.com", 26);

        when(userDao.findById(1L))
                .thenReturn(Optional.of(existingUser));
        when(userDao.findByEmail("new.anna@mail.com"))
                .thenReturn(Optional.empty());
        when(userDao.update(any(UserEntity.class)))
                .thenReturn(updatedUser);

        UserEntity result = userService.updateUser(request);

        assertEquals(updatedUser, result);
        verify(userDao).findById(1L);
        verify(userDao).findByEmail("new.anna@mail.com");
        verify(userDao).update(any(UserEntity.class));
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void updateUser_shouldThrowException_whenUserDoesNotExist() {
        UserUpdateRequest request = new UserUpdateRequest(999L, "Anna", "anna@mail.com", 25);

        when(userDao.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(request));

        verify(userDao).findById(999L);
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void updateUser_shouldThrowException_whenEmailAlreadyExists() {
        UserUpdateRequest request = new UserUpdateRequest(1L, "Anna", "taken@mail.com", 25);
        UserEntity existingUser = new UserEntity("Anna", "anna@mail.com", 25);
        UserEntity userWithSameEmail = new UserEntity("Bob", "taken@mail.com", 30);

        setId(existingUser, 1L);
        setId(userWithSameEmail, 2L);

        when(userDao.findById(1L))
                .thenReturn(Optional.of(existingUser));
        when(userDao.findByEmail("taken@mail.com"))
                .thenReturn(Optional.of(userWithSameEmail));

        assertThrows(InvalidInputException.class, () -> userService.updateUser(request));

        verify(userDao).findById(1L);
        verify(userDao).findByEmail("taken@mail.com");
        verify(userDao, never()).update(any(UserEntity.class));
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void updateUser_shouldThrowServiceException_whenDaoUpdateFails() {
        UserUpdateRequest request = new UserUpdateRequest(1L, "New Anna", "new.anna@mail.com", 26);
        UserEntity existingUser = new UserEntity("Anna", "anna@mail.com", 25);

        setId(existingUser, 1L);

        when(userDao.findById(1L))
                .thenReturn(Optional.of(existingUser));
        when(userDao.findByEmail("new.anna@mail.com"))
                .thenReturn(Optional.empty());
        when(userDao.update(any(UserEntity.class)))
                .thenThrow(new DataAccessException("DB error", new RuntimeException()));

        assertThrows(ServiceException.class, () -> userService.updateUser(request));

        verify(userDao).findById(1L);
        verify(userDao).findByEmail("new.anna@mail.com");
        verify(userDao).update(any(UserEntity.class));
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void deleteUser_shouldDeleteUser_whenUserExists() {
        when(userDao.deleteById(1L))
                .thenReturn(true);

        userService.deleteUser(1L);

        verify(userDao).deleteById(1L);
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void deleteUser_shouldThrowException_whenUserDoesNotExist() {
        when(userDao.deleteById(999L))
                .thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(999L));

        verify(userDao).deleteById(999L);
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void deleteUser_shouldThrowServiceException_whenDaoFails() {
        when(userDao.deleteById(1L))
                .thenThrow(new DataAccessException("DB error", new RuntimeException()));

        assertThrows(ServiceException.class, () -> userService.deleteUser(1L));

        verify(userDao).deleteById(1L);
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void hasUsers_shouldReturnTrue_whenUsersExist() {
        when(userDao.count())
                .thenReturn(2L);

        boolean result = userService.hasUsers();

        assertTrue(result);
        verify(userDao).count();
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void hasUsers_shouldReturnFalse_whenUsersDoNotExist() {
        when(userDao.count())
                .thenReturn(0L);

        boolean result = userService.hasUsers();

        assertFalse(result);
        verify(userDao).count();
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void hasUsers_shouldThrowServiceException_whenDaoFails() {
        when(userDao.count())
                .thenThrow(new DataAccessException("DB error", new RuntimeException()));

        assertThrows(ServiceException.class, () -> userService.hasUsers());

        verify(userDao).count();
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void ensureUserExists_shouldDoNothing_whenUserExists() {
        when(userDao.existsById(1L))
                .thenReturn(true);

        userService.ensureUserExists(1L);

        verify(userDao).existsById(1L);
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void ensureUserExists_shouldThrowException_whenUserDoesNotExist() {
        when(userDao.existsById(999L))
                .thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> userService.ensureUserExists(999L));

        verify(userDao).existsById(999L);
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void ensureUserExists_shouldThrowServiceException_whenDaoFails() {
        when(userDao.existsById(1L))
                .thenThrow(new DataAccessException("DB error", new RuntimeException()));

        assertThrows(ServiceException.class, () -> userService.ensureUserExists(1L));

        verify(userDao).existsById(1L);
        verifyNoMoreInteractions(userDao);
    }


    @Test
    void ensureEmailAvailable_shouldDoNothing_whenEmailIsAvailable() {
        when(userDao.findByEmail("anna@mail.com"))
                .thenReturn(Optional.empty());

        userService.ensureEmailAvailable("anna@mail.com", null);

        verify(userDao).findByEmail("anna@mail.com");
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void ensureEmailAvailable_shouldThrowException_whenEmailBelongsToAnotherUser() {
        UserEntity existingUser = new UserEntity("Bob", "anna@mail.com", 30);
        setId(existingUser, 2L);

        when(userDao.findByEmail("anna@mail.com"))
                .thenReturn(Optional.of(existingUser));

        assertThrows(InvalidInputException.class, () -> userService.ensureEmailAvailable("anna@mail.com", 1L));

        verify(userDao).findByEmail("anna@mail.com");
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void ensureEmailAvailable_shouldDoNothing_whenEmailBelongsToCurrentUser() {
        UserEntity existingUser = new UserEntity("Anna", "anna@mail.com", 25);
        setId(existingUser, 1L);

        when(userDao.findByEmail("anna@mail.com"))
                .thenReturn(Optional.of(existingUser));

        userService.ensureEmailAvailable("anna@mail.com", 1L);

        verify(userDao).findByEmail("anna@mail.com");
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void ensureEmailAvailable_shouldThrowServiceException_whenDaoFails() {
        when(userDao.findByEmail("anna@mail.com"))
                .thenThrow(new DataAccessException("DB error", new RuntimeException()));

        assertThrows(ServiceException.class, () -> userService.ensureEmailAvailable("anna@mail.com", null));

        verify(userDao).findByEmail("anna@mail.com");
        verifyNoMoreInteractions(userDao);
    }

    private static void setId(UserEntity userEntity, Long id) {
        try {
            Field idField = BaseEntity.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(userEntity, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException("Failed to set user id for test", e);
        }
    }
}
