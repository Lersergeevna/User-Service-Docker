package notificationservice.scheduler;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.ServerSetup;
import jakarta.mail.internet.MimeMessage;
import notificationservice.constants.Messages;
import notificationservice.entity.NotificationMessageEntity;
import notificationservice.entity.NotificationStatus;
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
 * Интеграционные тесты повторной отправки уведомлений о создании аккаунта.
 */
@SpringBootTest
@ActiveProfiles("test")
class CreatedNotificationRetrySchedulerIntegrationTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(new ServerSetup(3025, null, ServerSetup.PROTOCOL_SMTP))
            .withPerMethodLifecycle(false);

    @Autowired
    private CreatedNotificationRetryScheduler createdNotificationRetryScheduler;

    @Autowired
    private NotificationMessageRepository notificationMessageRepository;

    @BeforeEach
    void setUp() throws FolderException {
        greenMail.purgeEmailFromAllMailboxes();
        notificationMessageRepository.deleteAll();
    }

    @Test
    void retryCreatedNotifications_shouldSendOnlyUnsentCreatedMessages() throws Exception {
        NotificationMessageEntity createdFailed = new NotificationMessageEntity(
                "event-failed-1",
                "created-failed@example.com",
                UserOperation.CREATED,
                NotificationStatus.FAILED
        );
        createdFailed.markFailed("Предыдущая ошибка");

        NotificationMessageEntity createdPending = new NotificationMessageEntity(
                "event-pending-1",
                "created-pending@example.com",
                UserOperation.CREATED,
                NotificationStatus.PENDING
        );

        NotificationMessageEntity deletedFailed = new NotificationMessageEntity(
                "event-deleted-1",
                "deleted-failed@example.com",
                UserOperation.DELETED,
                NotificationStatus.FAILED
        );
        deletedFailed.markFailed("Предыдущая ошибка удаления");

        NotificationMessageEntity createdSent = new NotificationMessageEntity(
                "event-sent-1",
                "created-sent@example.com",
                UserOperation.CREATED,
                NotificationStatus.SENT
        );
        createdSent.markSent();

        notificationMessageRepository.saveAll(List.of(
                createdFailed,
                createdPending,
                deletedFailed,
                createdSent
        ));

        createdNotificationRetryScheduler.retryCreatedNotifications();

        List<NotificationMessageEntity> messages = notificationMessageRepository.findAll();
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();

        assertThat(receivedMessages).hasSize(2);
        assertThat(receivedMessages)
                .extracting(message -> message.getAllRecipients()[0].toString())
                .containsExactlyInAnyOrder(
                        "created-failed@example.com",
                        "created-pending@example.com"
                );

        assertThat(messages)
                .filteredOn(message -> message.getEmail().equals("created-failed@example.com"))
                .singleElement()
                .satisfies(message -> {
                    assertThat(message.getEventId()).isEqualTo("event-failed-1");
                    assertThat(message.getStatus()).isEqualTo(NotificationStatus.SENT);
                    assertThat(message.getSentAt()).isNotNull();
                    assertThat(message.getLastError()).isNull();
                });

        assertThat(messages)
                .filteredOn(message -> message.getEmail().equals("created-pending@example.com"))
                .singleElement()
                .satisfies(message -> {
                    assertThat(message.getEventId()).isEqualTo("event-pending-1");
                    assertThat(message.getStatus()).isEqualTo(NotificationStatus.SENT);
                    assertThat(message.getSentAt()).isNotNull();
                    assertThat(message.getLastError()).isNull();
                });

        assertThat(messages)
                .filteredOn(message -> message.getEmail().equals("deleted-failed@example.com"))
                .singleElement()
                .satisfies(message -> {
                    assertThat(message.getEventId()).isEqualTo("event-deleted-1");
                    assertThat(message.getStatus()).isEqualTo(NotificationStatus.FAILED);
                    assertThat(message.getSentAt()).isNull();
                });

        assertThat(messages)
                .filteredOn(message -> message.getEmail().equals("created-sent@example.com"))
                .singleElement()
                .satisfies(message -> {
                    assertThat(message.getEventId()).isEqualTo("event-sent-1");
                    assertThat(message.getStatus()).isEqualTo(NotificationStatus.SENT);
                    assertThat(message.getSentAt()).isNotNull();
                });

        assertThat(receivedMessages[0].getSubject()).isEqualTo(Messages.ACCOUNT_CREATED_SUBJECT);
        assertThat(receivedMessages[1].getSubject()).isEqualTo(Messages.ACCOUNT_CREATED_SUBJECT);
    }
}