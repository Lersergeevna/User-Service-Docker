package notificationservice.event;

/**
 * Тип операции над пользователем, получаемый из Kafka или REST API.
 */
public enum UserOperation {
    CREATED,
    DELETED
}
