package userservice.util;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExceptionUtilsTest {
    @Test
    void isConstraintViolation_shouldReturnTrue_whenCauseChainContainsConstraintViolation() {
        ConstraintViolationException constraintException =
                new ConstraintViolationException("Constraint failed", new SQLException(), "uk_users_email");

        RuntimeException exception = new RuntimeException("Wrapper", constraintException);

        assertTrue(ExceptionUtils.isConstraintViolation(exception));
    }

    @Test
    void isConstraintViolation_shouldReturnFalse_whenCauseChainDoesNotContainConstraintViolation() {
        RuntimeException exception = new RuntimeException("Some error");

        assertFalse(ExceptionUtils.isConstraintViolation(exception));
    }

    @Test
    void isEmailUniqueViolation_shouldReturnTrue_whenConstraintNameMatchesUsersEmailConstraint() {
        ConstraintViolationException exception =
                new ConstraintViolationException("Constraint failed", new SQLException(), "uk_users_email");

        assertTrue(ExceptionUtils.isEmailUniqueViolation(exception));
    }

    @Test
    void isEmailUniqueViolation_shouldReturnFalse_whenConstraintNameDoesNotMatchUsersEmailConstraint() {
        ConstraintViolationException exception =
                new ConstraintViolationException("Constraint failed", new SQLException(), "other_constraint");

        assertFalse(ExceptionUtils.isEmailUniqueViolation(exception));
    }
}