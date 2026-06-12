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
 * Базовый суперкласс для сущностей с идентификатором и временем создания.
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
     * Инициализирует поля аудита перед вставкой записи в базу данных.
     */
    @PrePersist
    protected void prePersistBase() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}