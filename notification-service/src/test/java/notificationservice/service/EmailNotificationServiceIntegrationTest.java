package notificationservice.service;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.ServerSetup;
import jakarta.mail.internet.MimeMessage;
import notificationservice.constants.Messages;
import notificationservice.event.UserNotificationEvent;
import notificationservice.event.UserOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты отправки email через реальный SMTP-сервер GreenMail.
 */
@SpringBootTest
@ActiveProfiles("test")
class EmailNotificationServiceIntegrationTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(new ServerSetup(3025, null, ServerSetup.PROTOCOL_SMTP))
            .withPerMethodLifecycle(false);

    @Autowired
    private EmailNotificationService emailNotificationService;

    @BeforeEach
    void setUp() throws FolderException {
        greenMail.purgeEmailFromAllMailboxes();
    }

    @Test
    void sendNotification_shouldSendCreatedAccountEmail() throws Exception {
        emailNotificationService.sendNotification(new UserNotificationEvent(UserOperation.CREATED, "alice@example.com"));

        MimeMessage[] messages = greenMail.getReceivedMessages();

        assertThat(messages).hasSize(1);
        assertThat(messages[0].getAllRecipients()[0].toString()).isEqualTo("alice@example.com");
        assertThat(messages[0].getSubject()).isEqualTo(Messages.ACCOUNT_CREATED_SUBJECT);
        assertThat(messages[0].getContent().toString())
                .contains(Messages.ACCOUNT_CREATED_TEXT);
    }

    @Test
    void sendNotification_shouldSendDeletedAccountEmail() throws Exception {
        emailNotificationService.sendNotification(new UserNotificationEvent(UserOperation.DELETED, "bob@example.com"));

        MimeMessage[] messages = greenMail.getReceivedMessages();

        assertThat(messages).hasSize(1);
        assertThat(messages[0].getAllRecipients()[0].toString()).isEqualTo("bob@example.com");
        assertThat(messages[0].getSubject()).isEqualTo(Messages.ACCOUNT_DELETED_SUBJECT);
        assertThat(messages[0].getContent().toString())
                .contains(Messages.ACCOUNT_DELETED_TEXT);
    }
}
