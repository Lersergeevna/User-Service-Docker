package userservice.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


import userservice.constants.Messages;
import userservice.dao.UserDao;
import userservice.dto.UserCreateRequest;
import userservice.dto.UserUpdateRequest;
import userservice.entity.BaseEntity;
import userservice.entity.UserEntity;
import userservice.event.UserNotificationEvent;
import userservice.event.UserOperation;
import userservice.exception.DataAccessException;
import userservice.exception.EntityNotFoundException;
import userservice.exception.InvalidInputException;
import userservice.exception.ServiceException;
import userservice.service.NotificationEventPublisher;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserDao userDao;

    @Mock
    private NotificationEventPublisher notificationEventPublisher;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userDao, notificationEventPublisher);
    }

    @Test
    void createUser_shouldReturnUserId_whenRequestIsValid() {
        UserCreateRequest request = new UserCreateRequest("Anna", "anna@mail.com", 25);
        Mockito.when(userDao.findByEmail("anna@mail.com"))
                .thenReturn(Optional.empty());
        Mockito.when(userDao.save(any(UserEntity.class)))
                .thenReturn(1L);

        Long result = userService.createUser(request);

        assertEquals(1L, result);
        Mockito.verify(userDao).findByEmail("anna@mail.com");
        Mockito.verify(userDao).save(any(UserEntity.class));
        Mockito.verifyNoMoreInteractions(userDao);
    }

    @Test
    void createUser_shouldThrowException_whenEmailAlreadyExists() {
        UserCreateRequest request = new UserCreateRequest("Anna", "anna@mail.com", 25);
        UserEntity existingUser = new UserEntity("Old Anna", "anna@mail.com", 30);

        Mockito.when(userDao.findByEmail("anna@mail.com"))
                .thenReturn(Optional.of(existingUser));

        assertThrows(InvalidInputException.class, () -> userService.createUser(request));

        Mockito.verify(userDao).findByEmail("anna@mail.com");
        Mockito.verify(userDao, Mockito.never()).save(any(UserEntity.class));
        Mockito.verifyNoMoreInteractions(userDao);
    }

    @Test
    void createUser_shouldThrowException_whenDaoSaveFails() {
        UserCreateRequest request = new UserCreateRequest("Anna", "anna@mail.com", 25);

        Mockito.when(userDao.findByEmail("anna@mail.com"))
                .thenReturn(Optional.empty());

        Mockito.when(userDao.save(any(UserEntity.class)))
                .thenThrow(new DataAccessException("DB error", new RuntimeException()));

        assertThrows(ServiceException.class, () -> userService.createUser(request));

        Mockito.verify(userDao).findByEmail("anna@mail.com");
        Mockito.verify(userDao).save(any(UserEntity.class));
        Mockito.verifyNoMoreInteractions(userDao);
    }

    @Test
    void getUserById_shouldReturnUser_whenUserExists() {
        UserEntity user = new UserEntity("Anna", "anna@mail.com", 25);

        Mockito.when(userDao.findById(1L))
                .thenReturn(Optional.of(user));

        UserEntity result = userService.getUserById(1L);

        assertEquals(user, result);

        Mockito.verify(userDao).findById(1L);
        Mockito.verifyNoMoreInteractions(userDao);
    }

    @Test
    void getUserById_shouldThrowException_whenUserDoesNotExist() {
        Mockito.when(userDao.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(999L));

        Mockito.verify(userDao).findById(999L);
        Mockito.verifyNoMoreInteractions(userDao);
    }

    @Test
    void getUserById_shouldThrowServiceException_whenDaoFails() {
        Mockito.when(userDao.findById(1L))
                .thenThrow(new DataAccessException("DB error", new RuntimeException()));

        assertThrows(ServiceException.class, () -> userService.getUserById(1L));

        Mockito.verify(userDao).findById(1L);
        Mockito.verifyNoMoreInteractions(userDao);
    }

    @Test
    void getAllUsers_shouldReturnUsers_whenDaoReturnsUsers() {
        List<UserEntity> users = List.of(
                new UserEntity("Anna", "anna@mail.com", 25),
                new UserEntity("Bob", "bob@mail.com", 30)
        );

        Mockito.when(userDao.findAll())
                .thenReturn(users);

        List<UserEntity> result = userService.getAllUsers();

        assertEquals(users, result);
        Mockito.verify(userDao).findAll();
        Mockito.verifyNoMoreInteractions(userDao);
    }

    @Test
    void getAllUsers_shouldThrowServiceException_whenDaoFails() {
        Mockito.when(userDao.findAll())
                .thenThrow(new DataAccessException("DB error", new RuntimeException()));

        assertThrows(ServiceException.class, () -> userService.getAllUsers());

        Mockito.verify(userDao).findAll();
        Mockito.verifyNoMoreInteractions(userDao);
    }

    @Test
    void updateUser_shouldReturnUpdatedUser_whenRequestIsValid() {
        UserUpdateRequest request = new UserUpdateRequest(1L, "New Anna", "new.anna@mail.com", 26);
        UserEntity existingUser = new UserEntity("Anna", "anna@mail.com", 25);
        UserEntity updatedUser = new UserEntity("New Anna", "new.anna@mail.com", 26);

        Mockito.when(userDao.findById(1L))
                .thenReturn(Optional.of(existingUser));
        Mockito.when(userDao.findByEmail("new.anna@mail.com"))
                .thenReturn(Optional.empty());
        Mockito.when(userDao.update(any(UserEntity.class)))
                .thenReturn(updatedUser);

        UserEntity result = userService.updateUser(request);

        assertEquals(updatedUser, result);
        Mockito.verify(userDao).findById(1L);
        Mockito.verify(userDao).findByEmail("new.anna@mail.com");
        Mockito.verify(userDao).update(any(UserEntity.class));
        Mockito.verifyNoMoreInteractions(userDao);
    }

    @Test
    void updateUser_shouldThrowException_whenUserDoesNotExist() {
        UserUpdateRequest request = new UserUpdateRequest(999L, "Anna", "anna@mail.com", 25);

        Mockito.when(userDao.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(request));

        Mockito.verify(userDao).findById(999L);
        Mockito.verifyNoMoreInteractions(userDao);
    }

    @Test
    void updateUser_shouldThrowException_whenEmailAlreadyExists() {
        UserUpdateRequest request = new UserUpdateRequest(1L, "Anna", "taken@mail.com", 25);
        UserEntity existingUser = new UserEntity("Anna", "anna@mail.com", 25);
        UserEntity userWithSameEmail = new UserEntity("Bob", "taken@mail.com", 30);

        setId(existingUser, 1L);
        setId(userWithSameEmail, 2L);

        Mockito.when(userDao.findById(1L))
                .thenReturn(Optional.of(existingUser));
        Mockito.when(userDao.findByEmail("taken@mail.com"))
                .thenReturn(Optional.of(userWithSameEmail));

        assertThrows(InvalidInputException.class, () -> userService.updateUser(request));

        Mockito.verify(userDao).findById(1L);
        Mockito.verify(userDao).findByEmail("taken@mail.com");
        Mockito.verify(userDao, Mockito.never()).update(any(UserEntity.class));
        Mockito.verifyNoMoreInteractions(userDao);
    }

    @Test
    void updateUser_shouldThrowServiceException_whenDaoUpdateFails() {
        UserUpdateRequest request = new UserUpdateRequest(1L, "New Anna", "new.anna@mail.com", 26);
        UserEntity existingUser = new UserEntity("Anna", "anna@mail.com", 25);

        setId(existingUser, 1L);

        Mockito.when(userDao.findById(1L))
                .thenReturn(Optional.of(existingUser));
        Mockito.when(userDao.findByEmail("new.anna@mail.com"))
                .thenReturn(Optional.empty());
        Mockito.when(userDao.update(any(UserEntity.class)))
                .thenThrow(new DataAccessException("DB error", new RuntimeException()));

        assertThrows(ServiceException.class, () -> userService.updateUser(request));

        Mockito.verify(userDao).findById(1L);
        Mockito.verify(userDao).findByEmail("new.anna@mail.com");
        Mockito.verify(userDao).update(any(UserEntity.class));
        Mockito.verifyNoMoreInteractions(userDao);
    }

    @Test
    void deleteUser_shouldDeleteUser_whenUserExists() {
        UserEntity existingUser = new UserEntity("Alice", "alice@example.com", 25);

        Mockito.when(userDao.findById(1L)).thenReturn(Optional.of(existingUser));
        Mockito.when(userDao.deleteById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        Mockito.verify(userDao).findById(1L);
        Mockito.verify(userDao).deleteById(1L);
        Mockito.verify(notificationEventPublisher).publish(
                new UserNotificationEvent(UserOperation.DELETED, "alice@example.com")
        );
        Mockito.verifyNoMoreInteractions(userDao, notificationEventPublisher);
    }

    @Test
    void deleteUser_shouldThrowException_whenUserDoesNotExist() {
        Mockito.when(userDao.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.deleteUser(999L)
        );

        assertEquals(Messages.USER_NOT_FOUND_BY_ID.formatted(999L), exception.getMessage());

        Mockito.verify(userDao).findById(999L);
        Mockito.verifyNoMoreInteractions(userDao);
        Mockito.verifyNoInteractions(notificationEventPublisher);
    }

    @Test
    void deleteUser_shouldThrowServiceException_whenDaoFails() {
        UserEntity existingUser = new UserEntity("Alice", "alice@example.com", 25);

        Mockito.when(userDao.findById(1L)).thenReturn(Optional.of(existingUser));
        Mockito.when(userDao.deleteById(1L))
                .thenThrow(new DataAccessException(
                        Messages.DATABASE_ERROR,
                        new RuntimeException(Messages.DATABASE_ERROR)
                ));
        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> userService.deleteUser(1L)
        );

        assertEquals(Messages.DELETE_FAILED, exception.getMessage());

        Mockito.verify(userDao).findById(1L);
        Mockito.verify(userDao).deleteById(1L);
        Mockito.verifyNoInteractions(notificationEventPublisher);
    }

    @Test
    void hasUsers_shouldReturnTrue_whenUsersExist() {
        Mockito.when(userDao.count())
                .thenReturn(2L);

        boolean result = userService.hasUsers();

        assertTrue(result);
        Mockito.verify(userDao).count();
        Mockito.verifyNoMoreInteractions(userDao);
    }

    @Test
    void hasUsers_shouldReturnFalse_whenUsersDoNotExist() {
        Mockito.when(userDao.count())
                .thenReturn(0L);

        boolean result = userService.hasUsers();

        assertFalse(result);
        Mockito.verify(userDao).count();
        Mockito.verifyNoMoreInteractions(userDao);
    }

    @Test
    void hasUsers_shouldThrowServiceException_whenDaoFails() {
        Mockito.when(userDao.count())
                .thenThrow(new DataAccessException("DB error", new RuntimeException()));

        assertThrows(ServiceException.class, () -> userService.hasUsers());

        Mockito.verify(userDao).count();
        Mockito.verifyNoMoreInteractions(userDao);
    }

    @Test
    void ensureUserExists_shouldDoNothing_whenUserExists() {
        Mockito.when(userDao.existsById(1L))
                .thenReturn(true);

        userService.ensureUserExists(1L);

        Mockito.verify(userDao).existsById(1L);
        Mockito.verifyNoMoreInteractions(userDao);
    }

    @Test
    void ensureUserExists_shouldThrowException_whenUserDoesNotExist() {
        Mockito.when(userDao.existsById(999L))
                .thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> userService.ensureUserExists(999L));

        Mockito.verify(userDao).existsById(999L);
        Mockito.verifyNoMoreInteractions(userDao);
    }

    @Test
    void ensureUserExists_shouldThrowServiceException_whenDaoFails() {
        Mockito.when(userDao.existsById(1L))
                .thenThrow(new DataAccessException("DB error", new RuntimeException()));

        assertThrows(ServiceException.class, () -> userService.ensureUserExists(1L));

        Mockito.verify(userDao).existsById(1L);
        Mockito.verifyNoMoreInteractions(userDao);
    }


    @Test
    void ensureEmailAvailable_shouldDoNothing_whenEmailIsAvailable() {
        Mockito.when(userDao.findByEmail("anna@mail.com"))
                .thenReturn(Optional.empty());

        userService.ensureEmailAvailable("anna@mail.com", null);

        Mockito.verify(userDao).findByEmail("anna@mail.com");
        Mockito.verifyNoMoreInteractions(userDao);
    }

    @Test
    void ensureEmailAvailable_shouldThrowException_whenEmailBelongsToAnotherUser() {
        UserEntity existingUser = new UserEntity("Bob", "anna@mail.com", 30);
        setId(existingUser, 2L);

        Mockito.when(userDao.findByEmail("anna@mail.com"))
                .thenReturn(Optional.of(existingUser));

        assertThrows(InvalidInputException.class, () -> userService.ensureEmailAvailable("anna@mail.com", 1L));

        Mockito.verify(userDao).findByEmail("anna@mail.com");
        Mockito.verifyNoMoreInteractions(userDao);
    }

    @Test
    void ensureEmailAvailable_shouldDoNothing_whenEmailBelongsToCurrentUser() {
        UserEntity existingUser = new UserEntity("Anna", "anna@mail.com", 25);
        setId(existingUser, 1L);

        Mockito.when(userDao.findByEmail("anna@mail.com"))
                .thenReturn(Optional.of(existingUser));

        userService.ensureEmailAvailable("anna@mail.com", 1L);

        Mockito.verify(userDao).findByEmail("anna@mail.com");
        Mockito.verifyNoMoreInteractions(userDao);
    }

    @Test
    void ensureEmailAvailable_shouldThrowServiceException_whenDaoFails() {
        Mockito.when(userDao.findByEmail("anna@mail.com"))
                .thenThrow(new DataAccessException("DB error", new RuntimeException()));

        assertThrows(ServiceException.class, () -> userService.ensureEmailAvailable("anna@mail.com", null));

        Mockito.verify(userDao).findByEmail("anna@mail.com");
        Mockito.verifyNoMoreInteractions(userDao);
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
