package userservice.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppPropertiesTest {
    @AfterEach
    void tearDown() {
        System.clearProperty("db.url");
        System.clearProperty("db.username");
        System.clearProperty("db.password");
    }

    @Test
    void dbUrl_shouldReturnSystemProperty_whenPropertyExists() {
        System.setProperty("db.url", "jdbc:postgresql://localhost:5434/test_db");

        String result = AppProperties.dbUrl();

        assertEquals("jdbc:postgresql://localhost:5434/test_db", result);
    }

    @Test
    void dbUsername_shouldReturnSystemProperty_whenPropertyExists() {
        System.setProperty("db.username", "test_user");

        String result = AppProperties.dbUsername();

        assertEquals("test_user", result);
    }

    @Test
    void dbPassword_shouldReturnSystemProperty_whenPropertyExists() {
        System.setProperty("db.password", "test_password");

        String result = AppProperties.dbPassword();

        assertEquals("test_password", result);
    }
}