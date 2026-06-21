package notificationservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import notificationservice.event.UserOperation;

import java.time.LocalDateTime;

/**
 * Сущность уведомления, которое нужно отправить или уже отправили пользователю.
 * Хранится для контроля статуса отправки и защиты от повторной обработки Kafka-событий.
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "notification_messages")
public class NotificationMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true, length = 36)
    private String eventId;

    @Column(nullable = false, length = 150)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UserOperation operation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationStatus status;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "last_error")
    private String lastError;

    /**
     * Создаёт уведомление с начальным статусом.
     *
     * @param eventId уникальный идентификатор Kafka-события
     * @param email e-mail получателя уведомления
     * @param operation операция, для которой отправляется уведомление
     * @param status начальный статус отправки
     */
    public NotificationMessageEntity(
            String eventId,
            String email,
            UserOperation operation,
            NotificationStatus status
    ) {
        this.eventId = eventId;
        this.email = email;
        this.operation = operation;
        this.status = status;
        this.retryCount = 0;
    }

    /**
     * Помечает уведомление как успешно отправленное.
     * Очищает последнюю ошибку и фиксирует время успешной отправки.
     */
    public void markSent() {
        this.status = NotificationStatus.SENT;
        this.sentAt = LocalDateTime.now();
        this.lastError = null;
    }

    /**
     * Помечает уведомление как неотправленное после ошибки.
     * Увеличивает счётчик попыток и сохраняет текст последней ошибки.
     *
     * @param errorMessage текст ошибки отправки
     */
    public void markFailed(String errorMessage) {
        this.status = NotificationStatus.FAILED;
        this.retryCount++;
        this.lastError = errorMessage;
    }

    /**
     * Заполняет дату создания перед первым сохранением сущности.
     */
    @PrePersist
    protected void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}