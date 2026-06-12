package notificationservice.constants;

/**
 * Хранит текстовые сообщения notification-service в одном месте.
 */
public final class Messages {

    public static final String ACCOUNT_CREATED_SUBJECT = "Аккаунт создан";
    public static final String ACCOUNT_DELETED_SUBJECT = "Аккаунт удалён";
    public static final String ACCOUNT_CREATED_TEXT = "Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.";
    public static final String ACCOUNT_DELETED_TEXT = "Здравствуйте! Ваш аккаунт был удалён.";

    public static final String OPERATION_REQUIRED = "Операция обязательна.";
    public static final String EMAIL_REQUIRED = "E-mail обязателен.";
    public static final String INVALID_EMAIL = "Неверный формат e-mail.";
    public static final String INVALID_NOTIFICATION_EVENT = "Событие уведомления отсутствует или содержит некорректные данные.";
    public static final String UNSUPPORTED_NOTIFICATION_OPERATION = "Неподдерживаемая операция уведомления.";
    public static final String EMAIL_SEND_FAILED = "Не удалось отправить email-уведомление.";

    public static final String VALIDATION_FAILED = "Ошибка валидации данных.";
    public static final String NULL_REQUEST = "Тело запроса отсутствует или некорректно.";
    public static final String INTERNAL_ERROR = "Внутренняя ошибка сервера.";
    public static final String STATUS_SENT = "ОТПРАВЛЕНО";
    private Messages() {
    }
}
