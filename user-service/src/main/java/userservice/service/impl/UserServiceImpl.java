package userservice.service.impl;

import lombok.NonNull;
import userservice.constants.Messages;
import userservice.dao.UserDao;
import userservice.dao.impl.UserDaoImpl;
import userservice.dto.UserCreateRequest;
import userservice.dto.UserUpdateRequest;
import userservice.entity.UserEntity;
import userservice.event.UserNotificationEvent;
import userservice.event.UserOperation;
import userservice.exception.DataAccessException;
import userservice.exception.EntityNotFoundException;
import userservice.exception.InvalidInputException;
import userservice.exception.ServiceException;
import userservice.mapper.UserMapper;
import userservice.service.NotificationEventPublisher;
import userservice.service.UserService;
import userservice.util.ExceptionUtils;
import userservice.util.Validators;

import java.util.List;

/**
 * Реализация сервисного слоя для работы с пользователями.
 * Выполняет валидацию входных данных, управляет CRUD-операциями через DAO
 * и публикует Kafka-события для notification-service при создании и удалении пользователя.
 */
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final NotificationEventPublisher notificationEventPublisher;

    /**
     * Создаёт сервис с реализацией DAO и Kafka publisher по умолчанию.
     */
    public UserServiceImpl() {
        this(new UserDaoImpl(), new KafkaNotificationEventPublisher());
    }

    /**
     * Создаёт сервис с переданным DAO и publisher-заглушкой.
     * Такой конструктор нужен для старых тестов, чтобы они не пытались подключаться к Kafka.
     *
     * @param userDao DAO для работы с пользователями
     */
    public UserServiceImpl(@NonNull UserDao userDao) {
        this(userDao, new NoOpNotificationEventPublisher());
    }

    /**
     * Создаёт сервис с переданными зависимостями.
     *
     * @param userDao DAO для работы с пользователями
     * @param notificationEventPublisher publisher событий уведомлений
     */
    public UserServiceImpl(
            @NonNull UserDao userDao,
            @NonNull NotificationEventPublisher notificationEventPublisher
    ) {
        this.userDao = userDao;
        this.notificationEventPublisher = notificationEventPublisher;
    }

    /**
     * Создаёт пользователя после валидации данных и проверки уникальности e-mail.
     * После успешного сохранения отправляет событие CREATED в Kafka.
     *
     * @param request данные для создания пользователя
     * @return идентификатор созданного пользователя
     * @throws InvalidInputException если данные некорректны или e-mail уже занят
     * @throws ServiceException если создать пользователя или отправить событие не удалось
     */
    @Override
    public Long createUser(UserCreateRequest request) {
        UserCreateRequest validRequest = validateCreateRequest(request);
        ensureEmailAvailable(validRequest.email(), null);

        try {
            UserEntity user = UserMapper.toEntity(validRequest);
            Long userId = userDao.save(user);
            publishUserCreatedEvent(user.getEmail());
            return userId;
        } catch (DataAccessException e) {
            if (ExceptionUtils.isEmailUniqueViolation(e)) {
                throw new InvalidInputException(Messages.DUPLICATE_EMAIL, e);
            }
            throw new ServiceException(Messages.CREATE_FAILED, e);
        }
    }

    /**
     * Возвращает пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     * @return найденный пользователь
     * @throws InvalidInputException если идентификатор некорректен
     * @throws EntityNotFoundException если пользователь не найден
     * @throws ServiceException если чтение пользователя из базы данных завершилось ошибкой
     */
    @Override
    public UserEntity getUserById(long id) {
        long validId = Validators.requireValidId(id);
        try {
            return userDao.findById(validId)
                    .orElseThrow(() ->
                            new EntityNotFoundException(Messages.USER_NOT_FOUND_BY_ID.formatted(validId)));
        } catch (DataAccessException e) {
            throw new ServiceException(Messages.READ_FAILED, e);
        }
    }

    /**
     * Возвращает список всех пользователей.
     *
     * @return список пользователей; если пользователей нет, возвращается пустой список
     * @throws ServiceException если чтение списка пользователей завершилось ошибкой
     */
    @Override
    public List<UserEntity> getAllUsers() {
        try {
            return userDao.findAll();
        } catch (DataAccessException e) {
            throw new ServiceException(Messages.READ_ALL_FAILED, e);
        }
    }

    /**
     * Обновляет пользователя после валидации данных и проверки уникальности e-mail.
     * Kafka-событие при обновлении не отправляется, потому что уведомления нужны только для создания и удаления.
     *
     * @param request данные для обновления пользователя
     * @return обновлённая сущность пользователя
     * @throws InvalidInputException если данные некорректны или e-mail уже занят
     * @throws EntityNotFoundException если пользователь не найден
     * @throws ServiceException если обновление пользователя завершилось ошибкой
     */
    @Override
    public UserEntity updateUser(UserUpdateRequest request) {
        UserUpdateRequest validRequest = validateUpdateRequest(request);
        UserEntity existing = getUserById(validRequest.id());
        ensureEmailAvailable(validRequest.email(), validRequest.id());

        try {
            UserEntity updated = UserMapper.applyUpdate(existing, validRequest);
            return userDao.update(updated);
        } catch (DataAccessException e) {
            if (ExceptionUtils.isEmailUniqueViolation(e)) {
                throw new InvalidInputException(Messages.DUPLICATE_EMAIL, e);
            }
            throw new ServiceException(Messages.UPDATE_FAILED, e);
        }
    }

    /**
     * Удаляет пользователя по идентификатору.
     * Перед удалением получает пользователя, чтобы сохранить e-mail для Kafka-события DELETED.
     *
     * @param id идентификатор пользователя
     * @throws InvalidInputException если идентификатор некорректен
     * @throws EntityNotFoundException если пользователь не найден
     * @throws ServiceException если удаление пользователя или отправка события завершились ошибкой
     */
    @Override
    public void deleteUser(long id) {
        long validId = Validators.requireValidId(id);
        UserEntity existingUser = getUserById(validId);

        try {
            boolean deleted = userDao.deleteById(validId);
            if (!deleted) {
                throw new EntityNotFoundException(Messages.USER_NOT_FOUND_BY_ID.formatted(validId));
            }
            publishUserDeletedEvent(existingUser.getEmail());
        } catch (DataAccessException e) {
            throw new ServiceException(Messages.DELETE_FAILED, e);
        }
    }

    /**
     * Проверяет, есть ли в базе данных хотя бы один пользователь.
     *
     * @return {@code true}, если пользователи есть; иначе {@code false}
     * @throws ServiceException если проверка количества пользователей завершилась ошибкой
     */
    @Override
    public boolean hasUsers() {
        try {
            return userDao.count() > 0;
        } catch (DataAccessException e) {
            throw new ServiceException(Messages.USERS_EXISTENCE_CHECK_FAILED, e);
        }
    }

    /**
     * Проверяет существование пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     * @throws InvalidInputException если идентификатор некорректен
     * @throws EntityNotFoundException если пользователь не найден
     * @throws ServiceException если проверка существования пользователя завершилась ошибкой
     */
    @Override
    public void ensureUserExists(long id) {
        long validId = Validators.requireValidId(id);
        try {
            if (!userDao.existsById(validId)) {
                throw new EntityNotFoundException(Messages.USER_NOT_FOUND_BY_ID.formatted(validId));
            }
        } catch (DataAccessException e) {
            throw new ServiceException(Messages.READ_FAILED, e);
        }
    }

    /**
     * Проверяет, что e-mail свободен для создания или обновления пользователя.
     * При обновлении допускает текущий e-mail того же пользователя.
     *
     * @param email проверяемый e-mail
     * @param currentUserId идентификатор текущего пользователя при обновлении или {@code null} при создании
     * @throws InvalidInputException если e-mail некорректен или уже занят другим пользователем
     * @throws ServiceException если проверка уникальности e-mail завершилась ошибкой
     */
    @Override
    public void ensureEmailAvailable(String email, Long currentUserId) {
        String validEmail = Validators.requireValidEmail(email);
        try {
            userDao.findByEmail(validEmail).ifPresent(userEntity -> {
                if (currentUserId == null || !userEntity.getId().equals(currentUserId)) {
                    throw new InvalidInputException(Messages.DUPLICATE_EMAIL);
                }
            });
        } catch (DataAccessException e) {
            throw new ServiceException(Messages.EMAIL_AVAILABILITY_CHECK_FAILED, e);
        }
    }

    /**
     * Публикует событие о создании пользователя.
     *
     * @param email e-mail созданного пользователя
     */
    private void publishUserCreatedEvent(String email) {
        notificationEventPublisher.publish(new UserNotificationEvent(UserOperation.CREATED, email));
    }

    /**
     * Публикует событие об удалении пользователя.
     *
     * @param email e-mail удалённого пользователя
     */
    private void publishUserDeletedEvent(String email) {
        notificationEventPublisher.publish(new UserNotificationEvent(UserOperation.DELETED, email));
    }

    /**
     * Проверяет запрос на создание пользователя и возвращает очищенный DTO с валидными значениями.
     *
     * @param request исходный запрос на создание пользователя
     * @return валидный запрос на создание пользователя
     * @throws InvalidInputException если запрос пустой или содержит некорректные значения
     */
    private UserCreateRequest validateCreateRequest(UserCreateRequest request) {
        if (request == null) {
            throw new InvalidInputException(Messages.NULL_REQUEST);
        }
        return new UserCreateRequest(
                Validators.requireValidName(request.name()),
                Validators.requireValidEmail(request.email()),
                Validators.requireValidAge(request.age())
        );
    }

    /**
     * Проверяет запрос на обновление пользователя и возвращает очищенный DTO с валидными значениями.
     *
     * @param request исходный запрос на обновление пользователя
     * @return валидный запрос на обновление пользователя
     * @throws InvalidInputException если запрос пустой или содержит некорректные значения
     */
    private UserUpdateRequest validateUpdateRequest(UserUpdateRequest request) {
        if (request == null) {
            throw new InvalidInputException(Messages.NULL_REQUEST);
        }
        return new UserUpdateRequest(
                Validators.requireValidId(request.id()),
                Validators.requireValidName(request.name()),
                Validators.requireValidEmail(request.email()),
                Validators.requireValidAge(request.age())
        );
    }
}
