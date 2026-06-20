package notificationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.ServerSetup;
import jakarta.mail.internet.MimeMessage;
import notificationservice.constants.Messages;
import notificationservice.dto.NotificationRequest;
import notificationservice.event.UserOperation;
import notificationservice.repository.NotificationMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Интеграционный тест REST API ручной отправки уведомления.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NotificationControllerIntegrationTest {

    private static final String NOTIFICATIONS_URL = "/api/v1/notifications";

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(new ServerSetup(3025, null, ServerSetup.PROTOCOL_SMTP))
            .withPerMethodLifecycle(false);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NotificationMessageRepository notificationMessageRepository;

    @BeforeEach
    void setUp() throws FolderException {
        greenMail.purgeEmailFromAllMailboxes();
        notificationMessageRepository.deleteAll();
    }

    @Test
    void sendNotification_shouldSendEmailAndReturnOk() throws Exception {
        NotificationRequest request = new NotificationRequest(UserOperation.DELETED, "alice@example.com");

        mockMvc.perform(post(NOTIFICATIONS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.status").value(Messages.STATUS_SENT));

        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertThat(messages).hasSize(1);
        assertThat(messages[0].getSubject()).isEqualTo(Messages.ACCOUNT_DELETED_SUBJECT);
        assertThat(messages[0].getContent().toString()).contains(Messages.ACCOUNT_DELETED_TEXT);
        assertThat(notificationMessageRepository.findAll()).isEmpty();
    }

    @Test
    void sendNotification_shouldReturnBadRequest_whenEmailIsInvalid() throws Exception {
        NotificationRequest request = new NotificationRequest(UserOperation.CREATED, "wrong-email");

        mockMvc.perform(post(NOTIFICATIONS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        assertThat(greenMail.getReceivedMessages()).isEmpty();
        assertThat(notificationMessageRepository.findAll()).isEmpty();
    }
}