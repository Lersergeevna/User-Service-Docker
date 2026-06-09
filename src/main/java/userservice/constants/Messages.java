package userservice.constants;

/**
 * Хранит текстовые сообщения приложения в одном месте.
 */
public final class Messages {

    public static final String USER_NOT_FOUND_BY_ID = "Пользователь с id=%d не найден.";
    public static final String DUPLICATE_EMAIL = "Пользователь с таким e-mail уже существует.";

    public static final String INVALID_NAME = "Имя не должно быть пустым и длиннее 100 символов.";
    public static final String INVALID_EMAIL = "Неверный формат e-mail.";
    public static final String INVALID_EMAIL_LENGTH = "E-mail не должен быть длиннее 150 символов.";
    public static final String INVALID_AGE = "Возраст должен быть в диапазоне от 1 до 130 лет.";

    public static final String NULL_REQUEST = "Тело запроса отсутствует или некорректно.";
    public static final String VALIDATION_FAILED = "Ошибка валидации данных.";
    public static final String INTERNAL_ERROR = "Внутренняя ошибка сервера.";

    private Messages() {
    }
}