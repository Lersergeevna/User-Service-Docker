package userservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import userservice.constants.Messages;
import userservice.dto.UserCreateRequest;
import userservice.dto.UserResponse;
import userservice.dto.UserUpdateRequest;
import userservice.entity.UserEntity;
import userservice.event.UserNotificationEvent;
import userservice.event.UserOperation;
import userservice.exception.DuplicateEmailException;
import userservice.exception.EntityNotFoundException;
import userservice.mapper.UserMapper;
import userservice.repository.UserRepository;
import userservice.service.UserService;

import java.util.List;

/**
 * Реализация бизнес-логики управления пользователями.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Создает пользователя, предварительно проверив уникальность e-mail.
     *
     * @param request данные нового пользователя
     * @return созданный пользователь
     */
    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        String email = userMapper.normalizeEmail(request.email());
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(Messages.DUPLICATE_EMAIL);
        }

        UserEntity userEntity = userMapper.toEntity(request.name(), email, request.age());
        UserEntity savedUser = userRepository.save(userEntity);

        applicationEventPublisher.publishEvent(
                new UserNotificationEvent(UserOperation.CREATED, savedUser.getEmail())
        );

        return userMapper.toResponse(savedUser);
    }

    /**
     * Возвращает пользователя по id.
     *
     * @param id идентификатор пользователя
     * @return найденный пользователь
     */
    @Override
    public UserResponse getUserById(long id) {
        return userMapper.toResponse(findUserById(id));
    }

    /**
     * Возвращает список всех пользователей.
     *
     * @return список пользователей
     */
    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    /**
     * Обновляет данные пользователя.
     *
     * @param id идентификатор пользователя
     * @param request новые данные пользователя
     * @return обновленный пользователь
     */
    @Override
    @Transactional
    public UserResponse updateUser(long id, UserUpdateRequest request) {
        UserEntity existingUser = findUserById(id);
        String email = userMapper.normalizeEmail(request.email());
        if (userRepository.existsByEmailAndIdNot(email, id)) {
            throw new DuplicateEmailException(Messages.DUPLICATE_EMAIL);
        }

        userMapper.updateEntity(existingUser, request);
        return userMapper.toResponse(existingUser);
    }

    /**
     * Удаляет пользователя, если он существует.
     *
     * @param id идентификатор пользователя
     */
    @Override
    @Transactional
    public void deleteUser(long id) {
        UserEntity existingUser = findUserById(id);
        userRepository.delete(existingUser);

        applicationEventPublisher.publishEvent(
                new UserNotificationEvent(UserOperation.DELETED, existingUser.getEmail())
        );
    }

    private UserEntity findUserById(long id) {
        if (id <= 0) {
            throw new EntityNotFoundException(Messages.USER_NOT_FOUND_BY_ID.formatted(id));
        }

        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Messages.USER_NOT_FOUND_BY_ID.formatted(id)));
    }
}