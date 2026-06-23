package userservice.constants;

public final class UserApiDoc {

    public static final String TAG_NAME = "Users";
    public static final String TAG_DESCRIPTION = "API для управления пользователями";
    public static final String USER_ID = "Идентификатор пользователя";

    public static final String GET_USER_BY_ID_SUMMARY = "Получить пользователя по id";
    public static final String USER_FOUND = "Пользователь найден";
    public static final String GET_USER_BY_ID_DESCRIPTION = "Возвращает пользователя по его идентификатору";
    public static final String GET_ALL_USERS = "Получить всех пользователей";
    public static final String RETURN_ALL_USERS_LIST = "Возвращает список всех пользователей";
    public static final String USERS_LIST_RETURNED = "Список пользователей получен";

    public static final String CREATE_USER_SUMMARY = "Создать пользователя";
    public static final String CREATE_USER_DESCRIPTION = "Создает нового пользователя по имени, email и возрасту";
    public static final String USER_CREATED = "Пользователь успешно создан";

    public static final String UPDATE_USER_SUMMARY = "Обновить пользователя";
    public static final String UPDATE_USER_DESCRIPTION = "Обновляет данные пользователя по его идентификатору";
    public static final String USER_UPDATED = "Пользователь успешно обновлен";

    public static final String DELETE_USER_SUMMARY = "Удалить пользователя";
    public static final String DELETE_USER_DESCRIPTION = "Удаляет пользователя по его идентификатору";
    public static final String USER_DELETED = "Пользователь успешно удален";

    public static final String BAD_REQUEST = "Некорректные данные запроса";
    public static final String USER_NOT_FOUND = "Пользователь не найден";
    public static final String EMAIL_CONFLICT = "Пользователь с таким email уже существует";

    private UserApiDoc() {
    }
}