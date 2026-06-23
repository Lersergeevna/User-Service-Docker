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
                new UserNotificationEvent("event-created-1", UserOperation.CREATED, "created@example.com")
        );

        List<NotificationMessageEntity> messages = notificationMessageRepository.findAll();
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();

        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getEventId()).isEqualTo("event-created-1");
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
    void processKafkaEvent_shouldSaveSentMessage_whenDeletedEventIsProcessed() {
        notificationMessageService.processKafkaEvent(
                new UserNotificationEvent("event-deleted-1", UserOperation.DELETED, "deleted@example.com")
        );

        List<NotificationMessageEntity> messages = notificationMessageRepository.findAll();

        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getEventId()).isEqualTo("event-deleted-1");
        assertThat(messages.get(0).getEmail()).isEqualTo("deleted@example.com");
        assertThat(messages.get(0).getOperation()).isEqualTo(UserOperation.DELETED);
        assertThat(messages.get(0).getStatus()).isEqualTo(NotificationStatus.SENT);
        assertThat(messages.get(0).getRetryCount()).isZero();
        assertThat(messages.get(0).getSentAt()).isNotNull();

        assertThat(greenMail.getReceivedMessages()).hasSize(1);
    }

    @Test
    void processKafkaEvent_shouldNotSendEmailTwice_whenSameEventIdIsProcessedTwice() throws Exception {
        UserNotificationEvent event = new UserNotificationEvent(
                "duplicate-event-1",
                UserOperation.CREATED,
                "duplicate@example.com"
        );

        notificationMessageService.processKafkaEvent(event);
        notificationMessageService.processKafkaEvent(event);

        List<NotificationMessageEntity> messages = notificationMessageRepository.findAll();
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();

        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getEventId()).isEqualTo("duplicate-event-1");
        assertThat(messages.get(0).getEmail()).isEqualTo("duplicate@example.com");
        assertThat(messages.get(0).getOperation()).isEqualTo(UserOperation.CREATED);
        assertThat(messages.get(0).getStatus()).isEqualTo(NotificationStatus.SENT);

        assertThat(receivedMessages).hasSize(1);
        assertThat(receivedMessages[0].getSubject()).isEqualTo(Messages.ACCOUNT_CREATED_SUBJECT);
        assertThat(receivedMessages[0].getContent().toString()).contains(Messages.ACCOUNT_CREATED_TEXT);
    }

    @Test
    void processKafkaEvent_shouldSendEmailsTwice_whenEventIdsAreDifferentForSameEmail() {
        UserNotificationEvent firstEvent = new UserNotificationEvent(
                "same-email-event-1",
                UserOperation.CREATED,
                "same-email@example.com"
        );

        UserNotificationEvent secondEvent = new UserNotificationEvent(
                "same-email-event-2",
                UserOperation.CREATED,
                "same-email@example.com"
        );

        notificationMessageService.processKafkaEvent(firstEvent);
        notificationMessageService.processKafkaEvent(secondEvent);

        List<NotificationMessageEntity> messages = notificationMessageRepository.findAll();
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();

        assertThat(messages).hasSize(2);
        assertThat(messages)
                .extracting(NotificationMessageEntity::getEventId)
                .containsExactlyInAnyOrder("same-email-event-1", "same-email-event-2");

        assertThat(receivedMessages).hasSize(2);
    }
}