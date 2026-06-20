package userservice.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import userservice.constants.Messages;
import userservice.dto.ErrorResponse;

import java.util.List;

/**
 * Централизованно преобразует исключения приложения в понятные HTTP-ответы.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает ошибку отсутствующей сущности.
     *
     * @param e исключение с описанием проблемы
     * @return ответ 404 Not Found
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException e) {
        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage(), List.of());
    }

    /**
     * Обрабатывает ошибку дублирующегося e-mail.
     *
     * @param e исключение с описанием проблемы
     * @return ответ 409 Conflict
     */
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmail(DuplicateEmailException e) {
        return buildResponse(HttpStatus.CONFLICT, e.getMessage(), List.of());
    }


    /**
     * Обрабатывает пустое или некорректное JSON-тело запроса.
     *
     * @param e исключение чтения HTTP-сообщения
     * @return ответ 400 Bad Request
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, Messages.NULL_REQUEST, List.of());
    }

    /**
     * Обрабатывает ошибки валидации request DTO.
     *
     * @param e исключение Spring Validation
     * @return ответ 400 Bad Request со списком ошибок по полям
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        List<String> details = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .distinct()
                .toList();
        return buildResponse(HttpStatus.BAD_REQUEST, Messages.VALIDATION_FAILED, details);
    }

    /**
     * Обрабатывает ошибки ограничения целостности данных на уровне БД.
     *
     * @param e исключение Spring Data
     * @return ответ 409 Conflict
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException e) {
        return buildResponse(HttpStatus.CONFLICT, Messages.DUPLICATE_EMAIL, List.of());
    }

    /**
     * Обрабатывает непредвиденные ошибки без раскрытия stack trace клиенту.
     *
     * @param e исходное исключение
     * @return ответ 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception e) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, Messages.INTERNAL_ERROR, List.of());
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message, List<String> details) {
        return ResponseEntity.status(status)
                .body(ErrorResponse.of(status.value(), status.getReasonPhrase(), message, details));
    }
}
