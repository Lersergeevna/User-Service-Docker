package notificationservice.repository;

import notificationservice.entity.NotificationMessageEntity;
import notificationservice.entity.NotificationStatus;
import notificationservice.event.UserOperation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

/**
 * Репозиторий уведомлений.
 */
public interface NotificationMessageRepository extends JpaRepository<NotificationMessageEntity, Long> {

    /**
     * Ищет первые 50 уведомлений по операции и набору статусов.
     *
     * @param operation операция уведомления
     * @param statuses статусы уведомлений
     * @return список уведомлений
     */
    List<NotificationMessageEntity> findTop50ByOperationAndStatusInOrderByCreatedAtAsc(
            UserOperation operation,
            Collection<NotificationStatus> statuses
    );
}