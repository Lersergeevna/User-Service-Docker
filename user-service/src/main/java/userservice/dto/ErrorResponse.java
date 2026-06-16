package userservice.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Единый формат ответа об ошибке для REST API.
 *
 * @param timestamp время формирования ошибки
 * @param status HTTP-статус числом
 * @param error краткое название HTTP-ошибки
 * @param message основное сообщение для клиента
 * @param details дополнительные детали, например ошибки валидации полей
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        List<String> details
) {

    /**
     * Создает ответ об ошибке с текущим временем.
     *
     * @param status HTTP-статус числом
     * @param error краткое название ошибки
     * @param message основное сообщение
     * @param details список деталей ошибки
     * @return заполненный {@code ErrorResponse}
     */
    public static ErrorResponse of(int status, String error, String message, List<String> details) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, details);
    }
}
