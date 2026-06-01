package userservice.util;

import org.junit.jupiter.api.Test;
import userservice.exception.InvalidInputException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidatorsTest {
    @Test
    void requireValidName_shouldReturnTrimmedName_whenNameIsValid() {
        String result = Validators.requireValidName("  Anna  ");

        assertEquals("Anna", result);
    }

    @Test
    void requireValidName_shouldThrowException_whenNameIsBlank() {
        assertThrows(InvalidInputException.class, () -> Validators.requireValidName("   "));
    }

    @Test
    void requireValidEmail_shouldReturnNormalizedEmail_whenEmailIsValid() {
        String result = Validators.requireValidEmail("  ANNA@MAIL.COM  ");

        assertEquals("anna@mail.com", result);
    }

    @Test
    void requireValidEmail_shouldThrowException_whenEmailFormatIsInvalid() {
        assertThrows(InvalidInputException.class, () -> Validators.requireValidEmail("anna-mail.com"));
    }

    @Test
    void requireValidAge_shouldReturnAge_whenAgeIsValid() {
        int result = Validators.requireValidAge(25);

        assertEquals(25, result);
    }

    @Test
    void requireValidAge_shouldThrowException_whenAgeIsLessThanOne() {
        assertThrows(InvalidInputException.class, () -> Validators.requireValidAge(0));
    }

    @Test
    void requireValidId_shouldReturnId_whenIdIsValid() {
        long result = Validators.requireValidId(1L);

        assertEquals(1L, result);
    }

    @Test
    void requireValidId_shouldThrowException_whenIdIsNotPositive() {
        assertThrows(InvalidInputException.class, () -> Validators.requireValidId(0L));
    }
}