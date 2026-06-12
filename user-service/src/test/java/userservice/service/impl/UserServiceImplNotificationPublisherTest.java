package userservice.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import userservice.dao.UserDao;
import userservice.dto.UserCreateRequest;
import userservice.entity.UserEntity;
import userservice.event.UserNotificationEvent;
import userservice.event.UserOperation;
import userservice.service.NotificationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Проверяет отправку Kafka-событий из user-service.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplNotificationPublisherTest {

    @Mock
    private UserDao userDao;

    @Mock
    private NotificationEventPublisher notificationEventPublisher;

    @Test
    void createUser_shouldPublishCreatedEvent() {
        UserServiceImpl userService = new UserServiceImpl(userDao, notificationEventPublisher);
        UserCreateRequest request = new UserCreateRequest("Alice", "alice@example.com", 25);

        when(userDao.findByEmail("alice@example.com")).thenReturn(Optional.empty());
        when(userDao.save(any(UserEntity.class))).thenReturn(1L);

        Long id = userService.createUser(request);

        assertEquals(1L, id);

        ArgumentCaptor<UserNotificationEvent> eventCaptor = ArgumentCaptor.forClass(UserNotificationEvent.class);
        verify(notificationEventPublisher).publish(eventCaptor.capture());

        UserNotificationEvent event = eventCaptor.getValue();
        assertEquals(UserOperation.CREATED, event.operation());
        assertEquals("alice@example.com", event.email());
    }

    @Test
    void deleteUser_shouldPublishDeletedEvent() {
        UserServiceImpl userService = new UserServiceImpl(userDao, notificationEventPublisher);
        UserEntity existingUser = new UserEntity("Bob", "bob@example.com", 30);

        when(userDao.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userDao.deleteById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        ArgumentCaptor<UserNotificationEvent> eventCaptor = ArgumentCaptor.forClass(UserNotificationEvent.class);
        verify(notificationEventPublisher).publish(eventCaptor.capture());

        UserNotificationEvent event = eventCaptor.getValue();
        assertEquals(UserOperation.DELETED, event.operation());
        assertEquals("bob@example.com", event.email());
    }
}