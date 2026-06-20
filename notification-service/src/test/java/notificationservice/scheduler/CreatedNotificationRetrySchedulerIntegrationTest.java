package notificationservice.scheduler;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.ServerSetup;
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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционный тест scheduler повторной отправки CREATED-уведомлений.
 */
@SpringBootTest
@ActiveProfiles("test")
class CreatedNotificationRetrySchedulerIntegrationTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(new ServerSetup(3025, null, ServerSetup.PROTOCOL_SMTP))
            .withPerMethodLifecycle(false);

    @Autowired
    private CreatedNotificationRetryScheduler scheduler;

    @Autowired
    private NotificationMessageRepository notificationMessageRepository;

    @BeforeEach
    void setUp() throws FolderException {
        greenMail.purgeEmailFromAllMailboxes();
        notificationMessageRepository.deleteAll();
    }

    @Test
    void retryCreatedNotifications_shouldSendOnlyUnsentCreatedMessages() {
        NotificationMessageEntity createdFailed = new NotificationMessageEntity(
                "created-failed@example.com",
                UserOperation.CREATED,
                NotificationStatus.FAILED
        );
        createdFailed.markFailed("Предыдущая ошибка");

        NotificationMessageEntity createdPending = new NotificationMessageEntity(
                "created-pending@example.com",
                UserOperation.CREATED,
                NotificationStatus.PENDING
        );

        NotificationMessageEntity deletedFailed = new NotificationMessageEntity(
                "deleted-failed@example.com",
                UserOperation.DELETED,
                NotificationStatus.FAILED
        );

        notificationMessageRepository.save(createdFailed);
        notificationMessageRepository.save(createdPending);
        notificationMessageRepository.save(deletedFailed);

        scheduler.retryCreatedNotifications();

        NotificationMessageEntity savedCreatedFailed = notificationMessageRepository
                .findById(createdFailed.getId())
                .orElseThrow();
        NotificationMessageEntity savedCreatedPending = notificationMessageRepository
                .findById(createdPending.getId())
                .orElseThrow();
        NotificationMessageEntity savedDeletedFailed = notificationMessageRepository
                .findById(deletedFailed.getId())
                .orElseThrow();

        assertThat(savedCreatedFailed.getStatus()).isEqualTo(NotificationStatus.SENT);
        assertThat(savedCreatedPending.getStatus()).isEqualTo(NotificationStatus.SENT);
        assertThat(savedDeletedFailed.getStatus()).isEqualTo(NotificationStatus.FAILED);

        assertThat(greenMail.getReceivedMessages()).hasSize(2);
    }
}