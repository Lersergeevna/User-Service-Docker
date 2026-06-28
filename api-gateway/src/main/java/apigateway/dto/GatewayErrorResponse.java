package apigateway.dto;

import java.time.LocalDateTime;

/**
 * Безопасный ответ gateway при недоступности внутреннего сервиса.
 *
 * @param timestamp время ошибки
 * @param status HTTP-статус
 * @param error краткое название ошибки
 * @param message безопасное сообщение для клиента
 */
public record GatewayErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message
) {
}