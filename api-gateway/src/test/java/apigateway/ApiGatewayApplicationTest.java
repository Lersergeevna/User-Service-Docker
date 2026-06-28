package apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Проверяет, что контекст api-gateway запускается.
 */
@SpringBootTest(properties = {
        "eureka.client.enabled=false",
        "spring.cloud.config.enabled=false"
})
class ApiGatewayApplicationTest {

    @Test
    void contextLoads() {
    }
}