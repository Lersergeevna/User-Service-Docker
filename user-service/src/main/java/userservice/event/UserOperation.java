package userservice.event;

/**
 * Операции с пользователем, о которых user-service сообщает в Kafka.
 */
public enum UserOperation {
    CREATED,
    DELETED
}
