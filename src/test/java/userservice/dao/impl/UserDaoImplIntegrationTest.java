package userservice.dao.impl;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import userservice.config.HibernateUtil;
import userservice.entity.UserEntity;
import userservice.exception.DataAccessException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
class UserDaoImplIntegrationTest {
    @Container
    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("user_service_test_db")
                    .withUsername("postgres")
                    .withPassword("postgres");

    private final UserDaoImpl userDao = new UserDaoImpl();

    @BeforeAll
    static void setUpDatabase() {
        System.setProperty("db.url", POSTGRES.getJdbcUrl());
        System.setProperty("db.username", POSTGRES.getUsername());
        System.setProperty("db.password", POSTGRES.getPassword());

        Flyway.configure()
                .dataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())
                .locations("classpath:db/migration")
                .load()
                .migrate();
    }

    @BeforeEach
    void cleanDatabase() throws Exception {
        try (Connection connection = DriverManager.getConnection(
                POSTGRES.getJdbcUrl(),
                POSTGRES.getUsername(),
                POSTGRES.getPassword()
        );
             Statement statement = connection.createStatement()) {
            statement.execute("truncate table users restart identity cascade");
        }
    }

    @AfterAll
    static void tearDown() {
        HibernateUtil.shutdown();
        System.clearProperty("db.url");
        System.clearProperty("db.username");
        System.clearProperty("db.password");
    }

    @Test
    void saveAndFindById_shouldPersistAndReturnUser() {
        UserEntity user = new UserEntity("Anna", "anna@mail.com", 25);

        Long id = userDao.save(user);
        Optional<UserEntity> result = userDao.findById(id);

        assertTrue(result.isPresent());
        assertEquals("Anna", result.get().getName());
        assertEquals("anna@mail.com", result.get().getEmail());
        assertEquals(25, result.get().getAge());
    }

    @Test
    void findByEmail_shouldReturnUser_whenEmailExists() {
        UserEntity user = new UserEntity("Anna", "anna@mail.com", 25);
        userDao.save(user);

        Optional<UserEntity> result = userDao.findByEmail("anna@mail.com");

        assertTrue(result.isPresent());
        assertEquals("Anna", result.get().getName());
        assertEquals("anna@mail.com", result.get().getEmail());
        assertEquals(25, result.get().getAge());
    }

    @Test
    void findAll_shouldReturnAllUsersOrderedById() {
        userDao.save(new UserEntity("Anna", "anna@mail.com", 25));
        userDao.save(new UserEntity("Bob", "bob@mail.com", 30));

        List<UserEntity> result = userDao.findAll();

        assertEquals(2, result.size());
        assertEquals("Anna", result.get(0).getName());
        assertEquals("Bob", result.get(1).getName());
    }

    @Test
    void count_shouldReturnUsersCount() {
        userDao.save(new UserEntity("Anna", "anna@mail.com", 25));
        userDao.save(new UserEntity("Bob", "bob@mail.com", 30));

        long result = userDao.count();

        assertEquals(2L, result);
    }

    @Test
    void existsById_shouldReturnTrue_whenUserExists() {
        Long id = userDao.save(new UserEntity("Anna", "anna@mail.com", 25));

        boolean result = userDao.existsById(id);

        assertTrue(result);
    }

    @Test
    void existsById_shouldReturnFalse_whenUserDoesNotExist() {
        boolean result = userDao.existsById(999L);

        assertFalse(result);
    }

    @Test
    void update_shouldChangeUserData() {
        Long id = userDao.save(new UserEntity("Anna", "anna@mail.com", 25));
        UserEntity user = userDao.findById(id).orElseThrow();

        user.setName("New Anna");
        user.setEmail("new.anna@mail.com");
        user.setAge(26);

        UserEntity updated = userDao.update(user);

        assertEquals("New Anna", updated.getName());
        assertEquals("new.anna@mail.com", updated.getEmail());
        assertEquals(26, updated.getAge());

        UserEntity found = userDao.findById(id).orElseThrow();
        assertEquals("New Anna", found.getName());
        assertEquals("new.anna@mail.com", found.getEmail());
        assertEquals(26, found.getAge());
    }

    @Test
    void deleteById_shouldDeleteUser_whenUserExists() {
        Long id = userDao.save(new UserEntity("Anna", "anna@mail.com", 25));

        boolean deleted = userDao.deleteById(id);
        Optional<UserEntity> result = userDao.findById(id);

        assertTrue(deleted);
        assertTrue(result.isEmpty());
    }

    @Test
    void save_shouldThrowDataAccessException_whenEmailAlreadyExists() {
        userDao.save(new UserEntity("Anna", "anna@mail.com", 25));

        assertThrows(DataAccessException.class, () ->
                userDao.save(new UserEntity("Bob", "anna@mail.com", 30))
        );
    }
}