package userservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;

/**
 * Сущность пользователя.
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "UserEntity")
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(name = "uk_users_email", columnNames = "email")
)
@Check(constraints = "age between 1 and 130")
public class UserEntity extends BaseEntity {
    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false)
    private Integer age;

    // Создаёт нового пользователя.
    public UserEntity(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }
}