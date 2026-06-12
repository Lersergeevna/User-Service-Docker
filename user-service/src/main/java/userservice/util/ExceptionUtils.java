package userservice.util;

import org.hibernate.exception.ConstraintViolationException;

import java.sql.SQLException;
import java.util.Locale;

/**
 * Содержит вспомогательные методы для анализа цепочки исключений.
 */
public final class ExceptionUtils {
    private static final String USERS_EMAIL_CONSTRAINT = "uk_users_email";

    private ExceptionUtils() {
    }

    /**
     * Проверяет, содержит ли цепочка исключений ошибку нарушения ограничения базы данных.
     *
     * @param throwable исключение для анализа
     * @return {@code true}, если найдено нарушение ограничения базы данных
     */
    public static boolean isConstraintViolation(Throwable throwable) {
        return findConstraintViolation(throwable) != null;
    }

    /**
     * Проверяет, связано ли исключение с уникальностью e-mail.
     *
     * @param throwable исключение для анализа
     * @return {@code true}, если нарушено ограничение уникальности e-mail
     */
    public static boolean isEmailUniqueViolation(Throwable throwable) {
        ConstraintViolationException exception = findConstraintViolation(throwable);
        if (exception == null) {
            return false;
        }

        String constraintName = exception.getConstraintName();
        if (constraintName != null && USERS_EMAIL_CONSTRAINT.equalsIgnoreCase(constraintName)) {
            return true;
        }

        return containsIgnoreCase(exception.getMessage(), USERS_EMAIL_CONSTRAINT)
                || containsIgnoreCase(sqlStateMessage(exception.getSQLException()), USERS_EMAIL_CONSTRAINT);
    }

    private static ConstraintViolationException findConstraintViolation(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof ConstraintViolationException constraintViolationException) {
                return constraintViolationException;
            }
            current = current.getCause();
        }
        return null;
    }

    private static String sqlStateMessage(SQLException exception) {
        if (exception == null) {
            return null;
        }
        return exception.getMessage();
    }

    private static boolean containsIgnoreCase(String source, String target) {
        return source != null && source.toLowerCase(Locale.ROOT).contains(target.toLowerCase(Locale.ROOT));
    }
}
