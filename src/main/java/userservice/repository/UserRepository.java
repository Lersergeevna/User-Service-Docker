package userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import userservice.entity.UserEntity;

import java.util.Optional;

/**
 * Репозиторий для работы с пользователями через Spring Data JPA.
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Ищет пользователя по e-mail.
     *
     * @param email нормализованный e-mail
     * @return найденный пользователь или пустой Optional
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Проверяет, занят ли e-mail.
     *
     * @param email нормализованный e-mail
     * @return {@code true}, если пользователь с таким e-mail существует
     */
    boolean existsByEmail(String email);

    /**
     * Проверяет, занят ли e-mail другим пользователем.
     *
     * <p>Используется при обновлении, чтобы пользователь мог оставить свой текущий e-mail.</p>
     *
     * @param email нормализованный e-mail
     * @param id id пользователя, которого нужно исключить из проверки
     * @return {@code true}, если e-mail принадлежит другому пользователю
     */
    boolean existsByEmailAndIdNot(String email, Long id);
}
