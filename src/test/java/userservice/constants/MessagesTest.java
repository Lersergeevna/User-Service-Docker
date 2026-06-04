package userservice.constants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessagesTest {
    @Test
    void formatError_shouldAddErrorPrefix() {
        String result = Messages.formatError("Ошибка");

        assertEquals("[ОШИБКА] Ошибка", result);
    }

    @Test
    void formatSuccess_shouldAddSuccessPrefix() {
        String result = Messages.formatSuccess("Готово");

        assertEquals("[УСПЕХ] Готово", result);
    }

    @Test
    void formatInfo_shouldAddInfoPrefix() {
        String result = Messages.formatInfo("Информация");

        assertEquals("[ИНФО] Информация", result);
    }
}