package userservice.event;

/**
 * Тип операции над пользователем, отправляемый в Kafka.
 */
public enum UserOperation {
    CREATED,
    DELETED
}