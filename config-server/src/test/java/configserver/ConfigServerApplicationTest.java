package configserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Проверяет, что контекст config-server запускается.
 */
@SpringBootTest(properties = {
        "eureka.client.enabled=false"
})
class ConfigServerApplicationTest {

    @Test
    void contextLoads() {
    }
}