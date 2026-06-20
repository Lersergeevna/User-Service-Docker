package userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import userservice.dto.UserCreateRequest;
import userservice.dto.UserUpdateRequest;
import userservice.repository.UserRepository;
import userservice.service.notification.NotificationEventPublisher;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Интеграционные API-тесты для {@link UserController} через MockMvc.
 *
 * <p>Тесты проверяют полный путь запроса: controller → service → repository → test DB.
 * Для изоляции перед каждым тестом таблица пользователей очищается.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    private static final String USERS_URL = "/api/v1/users";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private NotificationEventPublisher notificationEventPublisher;

    /**
     * Очищает таблицу перед каждым тестом, чтобы тесты не зависели друг от друга.
     */
    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    /**
     * Проверяет создание пользователя и нормализацию e-mail.
     */
    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {
        UserCreateRequest request = new UserCreateRequest("Alice", "Alice@Example.com", 25);

        mockMvc.perform(post(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.age").value(25));
    }

    /**
     * Проверяет получение пользователя по id.
     */
    @Test
    void getUserById_shouldReturnUser() throws Exception {
        Long userId = createUserAndReturnId("Bob", "bob@example.com", 31);

        mockMvc.perform(get(USERS_URL + "/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Bob"))
                .andExpect(jsonPath("$.email").value("bob@example.com"))
                .andExpect(jsonPath("$.age").value(31));
    }

    /**
     * Проверяет получение списка пользователей.
     *
     * <p>Порядок элементов в списке не проверяется, потому что repository.findAll()
     * не обязан возвращать записи в фиксированном порядке.</p>
     */
    @Test
    void getAllUsers_shouldReturnUsers() throws Exception {
        createUserAndReturnId("Alice", "alice@example.com", 25);
        createUserAndReturnId("Bob", "bob@example.com", 31);

        mockMvc.perform(get(USERS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Alice", "Bob")))
                .andExpect(jsonPath("$[*].email", containsInAnyOrder("alice@example.com", "bob@example.com")))
                .andExpect(jsonPath("$[*].age", containsInAnyOrder(25, 31)));
    }

    /**
     * Проверяет обновление данных пользователя.
     */
    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        Long userId = createUserAndReturnId("Alice", "alice@example.com", 25);
        UserUpdateRequest request = new UserUpdateRequest("Alice Updated", "alice.updated@example.com", 26);

        mockMvc.perform(put(USERS_URL + "/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Alice Updated"))
                .andExpect(jsonPath("$.email").value("alice.updated@example.com"))
                .andExpect(jsonPath("$.age").value(26));
    }

    /**
     * Проверяет ошибку 404 при попытке обновить несуществующего пользователя.
     */
    @Test
    void updateUser_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest("Alice", "alice@example.com", 25);

        mockMvc.perform(put(USERS_URL + "/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    /**
     * Проверяет удаление пользователя.
     */
    @Test
    void deleteUser_shouldRemoveUser() throws Exception {
        Long userId = createUserAndReturnId("Alice", "alice@example.com", 25);

        mockMvc.perform(delete(USERS_URL + "/{id}", userId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(USERS_URL + "/{id}", userId))
                .andExpect(status().isNotFound());
    }

    /**
     * Проверяет ошибку 404 при попытке удалить несуществующего пользователя.
     */
    @Test
    void deleteUser_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        mockMvc.perform(delete(USERS_URL + "/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    /**
     * Проверяет ошибку 400 при невалидном теле запроса.
     */
    @Test
    void createUser_shouldReturnBadRequest_whenBodyIsInvalid() throws Exception {
        UserCreateRequest request = new UserCreateRequest("", "wrong-email", 0);

        mockMvc.perform(post(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details", not(nullValue())));
    }

    /**
     * Проверяет ошибку 409 при попытке создать пользователя с занятым e-mail.
     */
    @Test
    void createUser_shouldReturnConflict_whenEmailAlreadyExists() throws Exception {
        createUserAndReturnId("Alice", "alice@example.com", 25);
        UserCreateRequest request = new UserCreateRequest("Another Alice", "alice@example.com", 30);

        mockMvc.perform(post(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    /**
     * Проверяет ошибку 404, если пользователь не найден.
     */
    @Test
    void getUserById_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        mockMvc.perform(get(USERS_URL + "/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    private Long createUserAndReturnId(String name, String email, int age) throws Exception {
        String response = mockMvc.perform(post(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserCreateRequest(name, email, age))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }
}