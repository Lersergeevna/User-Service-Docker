package userservice.dao.impl;

import lombok.NonNull;
import userservice.config.HibernateUtil;
import userservice.constants.Messages;
import userservice.dao.UserDao;
import userservice.entity.UserEntity;
import userservice.exception.DataAccessException;
import userservice.util.ExceptionUtils;

import java.util.List;
import java.util.Optional;

/**
 * Реализация DAO для работы с сущностью пользователя через Hibernate.
 */
public class UserDaoImpl implements UserDao {
    /**
     * Сохраняет пользователя в базе данных и возвращает его идентификатор.
     *
     * @param userEntity сохраняемая сущность пользователя
     * @return идентификатор сохранённого пользователя
     * @throws DataAccessException если сохранение завершилось ошибкой
     */
    @Override
    public Long save(@NonNull UserEntity userEntity) {
        try {
            return HibernateUtil.executeInTransaction(session -> {
                session.persist(userEntity);
                return userEntity.getId();
            });
        } catch (RuntimeException e) {
            throw dataAccessException(e, Messages.SAVE_DB_FAILED);
        }
    }

    /**
     * Ищет пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     * @return найденный пользователь или пустой Optional
     * @throws DataAccessException если поиск завершился ошибкой
     */
    @Override
    public Optional<UserEntity> findById(@NonNull Long id) {
        try {
            return HibernateUtil.executeWithoutTransaction(
                    session -> Optional.ofNullable(session.get(UserEntity.class, id))
            );
        } catch (RuntimeException e) {
            throw new DataAccessException(Messages.FIND_BY_ID_DB_FAILED, e);
        }
    }

    /**
     * Ищет пользователя по e-mail.
     *
     * @param email e-mail пользователя
     * @return найденный пользователь или пустой Optional
     * @throws DataAccessException если поиск завершился ошибкой
     */
    @Override
    public Optional<UserEntity> findByEmail(@NonNull String email) {
        try {
            return HibernateUtil.executeWithoutTransaction(session ->
                    session.createQuery(
                                    "select u from UserEntity u where u.email = :email", UserEntity.class)
                            .setParameter("email", email)
                            .uniqueResultOptional()
            );
        } catch (RuntimeException e) {
            throw new DataAccessException(Messages.FIND_BY_EMAIL_DB_FAILED, e);
        }
    }

    /**
     * Возвращает всех пользователей, отсортированных по идентификатору.
     *
     * @return список пользователей
     * @throws DataAccessException если чтение списка завершилось ошибкой
     */
    @Override
    public List<UserEntity> findAll() {
        try {
            return HibernateUtil.executeWithoutTransaction(session ->
                    session.createQuery("select u from UserEntity u order by u.id", UserEntity.class)
                            .getResultList()
            );
        } catch (RuntimeException e) {
            throw new DataAccessException(Messages.FIND_ALL_DB_FAILED, e);
        }
    }

    /**
     * Возвращает количество пользователей в базе данных.
     *
     * @return количество пользователей
     * @throws DataAccessException если подсчёт завершился ошибкой
     */
    @Override
    public long count() {
        try {
            return HibernateUtil.executeWithoutTransaction(session ->
                    session.createSelectionQuery("select count(u.id) from UserEntity u", Long.class)
                            .getSingleResult()
            );
        } catch (RuntimeException e) {
            throw new DataAccessException(Messages.COUNT_DB_FAILED, e);
        }
    }

    /**
     * Проверяет существование пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     * @return true, если пользователь существует
     * @throws DataAccessException если проверка завершилась ошибкой
     */
    @Override
    public boolean existsById(@NonNull Long id) {
        try {
            return HibernateUtil.executeWithoutTransaction(session ->
                    session.createSelectionQuery(
                                    "select count(u.id) from UserEntity u where u.id = :id", Long.class)
                            .setParameter("id", id)
                            .getSingleResult() > 0
            );
        } catch (RuntimeException e) {
            throw new DataAccessException(Messages.FIND_BY_ID_DB_FAILED, e);
        }
    }

    /**
     * Обновляет пользователя в базе данных.
     *
     * @param userEntity сущность пользователя с новыми значениями
     * @return обновлённая сущность пользователя
     * @throws DataAccessException если обновление завершилось ошибкой
     */
    @Override
    public UserEntity update(@NonNull UserEntity userEntity) {
        try {
            return HibernateUtil.executeInTransaction(session -> session.merge(userEntity));
        } catch (RuntimeException e) {
            throw dataAccessException(e, Messages.UPDATE_DB_FAILED);
        }
    }

    /**
     * Удаляет пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     * @return true, если пользователь был найден и удалён
     * @throws DataAccessException если удаление завершилось ошибкой
     */
    @Override
    public boolean deleteById(@NonNull Long id) {
        try {
            return HibernateUtil.executeInTransaction(session -> {
                UserEntity userEntity = session.get(UserEntity.class, id);
                if (userEntity == null) {
                    return false;
                }
                session.remove(userEntity);
                return true;
            });
        } catch (RuntimeException e) {
            throw new DataAccessException(Messages.DELETE_DB_FAILED, e);
        }
    }

    /**
     * Создаёт DataAccessException с отдельным сообщением для нарушения ограничений БД.
     *
     * @param e исходная runtime-ошибка Hibernate или JDBC
     * @param fallbackMessage сообщение по умолчанию
     * @return исключение доступа к данным
     */
    private DataAccessException dataAccessException(RuntimeException e, String fallbackMessage) {
        if (ExceptionUtils.isConstraintViolation(e)) {
            return new DataAccessException(Messages.DB_CONSTRAINT_FAILED, e);
        }
        return new DataAccessException(fallbackMessage, e);
    }
}
