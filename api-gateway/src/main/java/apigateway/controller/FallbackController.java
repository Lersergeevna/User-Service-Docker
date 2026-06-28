package apigateway.controller;

import apigateway.constants.GatewayMessages;
import apigateway.dto.GatewayErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * Возвращает безопасные fallback-ответы,
 * если внутренний сервис временно недоступен.
 */
@RestController
public class FallbackController {

    @RequestMapping("/fallback/user-service")
    public ResponseEntity<GatewayErrorResponse> userServiceFallback() {
        return buildServiceUnavailableResponse(GatewayMessages.USER_SERVICE_UNAVAILABLE);
    }

    @RequestMapping("/fallback/notification-service")
    public ResponseEntity<GatewayErrorResponse> notificationServiceFallback() {
        return buildServiceUnavailableResponse(GatewayMessages.NOTIFICATION_SERVICE_UNAVAILABLE);
    }

    private ResponseEntity<GatewayErrorResponse> buildServiceUnavailableResponse(String message) {
        GatewayErrorResponse response = new GatewayErrorResponse(
                LocalDateTime.now(),
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                GatewayMessages.SERVICE_UNAVAILABLE,
                message
        );

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}