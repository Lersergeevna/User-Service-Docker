package userservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Базовый класс для JPA-сущностей с общими техническими полями.
 */
@Getter
@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Заполняет дату создания перед первым сохранением сущности.
     */
    @PrePersist
    protected void prePersistBase() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
