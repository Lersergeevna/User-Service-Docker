package userservice.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import userservice.exception.InputClosedException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InputReaderTest {
    private final InputStream originalIn = System.in;

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
    }

    @Test
    void readNonBlank_shouldReturnValue_whenInputIsValid() {
        System.setIn(new ByteArrayInputStream("Anna%n".formatted().getBytes()));

        try (InputReader inputReader = new InputReader()) {
            String result = inputReader.readNonBlank("Введите значение: ");

            assertEquals("Anna", result);
        }
    }

    @Test
    void readAge_shouldReturnAge_whenInputIsValid() {
        System.setIn(new ByteArrayInputStream("25%n".formatted().getBytes()));

        try (InputReader inputReader = new InputReader()) {
            int result = inputReader.readAge("Введите возраст: ");

            assertEquals(25, result);
        }
    }

    @Test
    void readPositiveId_shouldReturnId_whenInputIsValid() {
        System.setIn(new ByteArrayInputStream("10%n".formatted().getBytes()));

        try (InputReader inputReader = new InputReader()) {
            long result = inputReader.readPositiveId("Введите id: ");

            assertEquals(10L, result);
        }
    }

    @Test
    void readConfirmation_shouldReturnTrue_whenInputIsY() {
        System.setIn(new ByteArrayInputStream("Y%n".formatted().getBytes()));

        try (InputReader inputReader = new InputReader()) {
            boolean result = inputReader.readConfirmation("Подтвердите: ");

            assertTrue(result);
        }
    }

    @Test
    void readNonBlank_shouldThrowInputClosedException_whenInputIsClosed() {
        System.setIn(new ByteArrayInputStream(new byte[0]));

        try (InputReader inputReader = new InputReader()) {
            assertThrows(InputClosedException.class, () -> inputReader.readNonBlank("Введите значение: "));
        }
    }
}