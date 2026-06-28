package apigateway.constants;

/**
 * Сообщения, которые api-gateway возвращает клиенту.
 */
public final class GatewayMessages {

    public static final String SERVICE_UNAVAILABLE = "Service Unavailable";
    public static final String USER_SERVICE_UNAVAILABLE = "Сервис пользователей временно недоступен.";
    public static final String NOTIFICATION_SERVICE_UNAVAILABLE = "Сервис уведомлений временно недоступен.";

    private GatewayMessages() {
    }
}