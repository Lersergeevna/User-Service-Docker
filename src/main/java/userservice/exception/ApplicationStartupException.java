package userservice.exception;

/**
 * Ошибка инициализации приложения: миграции, подключение к базе данных или Hibernate.
 */
public class ApplicationStartupException extends RuntimeException {
    public ApplicationStartupException(String message, Throwable cause) {
        super(message, cause);
    }
}
