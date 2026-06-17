package notificationservice.exception;

import notificationservice.constants.Messages;
import notificationservice.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Централизованно преобразует исключения notification-service в HTTP-ответы.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidNotificationEventException.class)
    public ResponseEntity<ErrorResponse> handleInvalidNotificationEvent(InvalidNotificationEventException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage(), List.of());
    }

    @ExceptionHandler(UnsupportedNotificationOperationException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedOperation(UnsupportedNotificationOperationException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage(), List.of());
    }

    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<ErrorResponse> handleEmailSending(EmailSendingException e) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), List.of());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, Messages.NULL_REQUEST, List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        List<String> details = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .distinct()
                .toList();
        return buildResponse(HttpStatus.BAD_REQUEST, Messages.VALIDATION_FAILED, details);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception e) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, Messages.INTERNAL_ERROR, List.of());
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message, List<String> details) {
        return ResponseEntity.status(status)
                .body(ErrorResponse.of(status.value(), status.getReasonPhrase(), message, details));
    }
}
