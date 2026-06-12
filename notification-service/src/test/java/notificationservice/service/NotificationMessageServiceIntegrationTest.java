package notificationservice.service;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.ServerSetup;
import jakarta.mail.internet.MimeMessage;
import notificationservice.constants.Messages;
import notificationservice.entity.NotificationMessageEntity;
import notificationservice.entity.NotificationStatus;
import notificationservice.event.UserNotificationEvent;
import notificationservice.event.UserOperation;
import notificationservice.repository.NotificationMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты хранения статусов уведомлений.
 */
@SpringBootTest
@ActiveProfiles("test")
class NotificationMessageServiceIntegrationTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(new ServerSetup(3025, null, ServerSetup.PROTOCOL_SMTP))
            .withPerMethodLifecycle(false);

    @Autowired
    private NotificationMessageService notificationMessageService;

    @Autowired
    private NotificationMessageRepository notificationMessageRepository;

    @BeforeEach
    void setUp() throws FolderException {
        greenMail.purgeEmailFromAllMailboxes();
        notificationMessageRepository.deleteAll();
    }

    @Test
    void processKafkaEvent_shouldSaveSentMessage_whenCreatedEventIsProcessed() throws Exception {
        notificationMessageService.processKafkaEvent(
                new UserNotificationEvent(UserOperation.CREATED, "created@example.com")
        );

        List<NotificationMessageEntity> messages = notificationMessageRepository.findAll();
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();

        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getEmail()).isEqualTo("created@example.com");
        assertThat(messages.get(0).getOperation()).isEqualTo(UserOperation.CREATED);
        assertThat(messages.get(0).getStatus()).isEqualTo(NotificationStatus.SENT);
        assertThat(messages.get(0).getRetryCount()).isZero();
        assertThat(messages.get(0).getSentAt()).isNotNull();

        assertThat(receivedMessages).hasSize(1);
        assertThat(receivedMessages[0].getSubject()).isEqualTo(Messages.ACCOUNT_CREATED_SUBJECT);
        assertThat(receivedMessages[0].getContent().toString()).contains(Messages.ACCOUNT_CREATED_TEXT);
    }

    @Test
    void processKafkaEvent_shouldNotSaveMessage_whenDeletedEventIsProcessed() {
        notificationMessageService.processKafkaEvent(
                new UserNotificationEvent(UserOperation.DELETED, "deleted@example.com")
        );

        assertThat(notificationMessageRepository.findAll()).isEmpty();
        assertThat(greenMail.getReceivedMessages()).hasSize(1);
    }
}