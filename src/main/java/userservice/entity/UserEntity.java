package userservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.Check;

// Сущность пользователя.
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

    // Конструктор без аргументов, необходимый Hibernate.
    protected UserEntity() {
    }

    // Создаёт нового пользователя.
    public UserEntity(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    // Возвращает имя пользователя.
    public String getName() {
        return name;
    }

    // Устанавливает имя пользователя.
    public void setName(String name) {
        this.name = name;
    }

    //Возвращает e-mail пользователя.
    public String getEmail() {
        return email;
    }

    // Устанавливает e-mail пользователя.
    public void setEmail(String email) {
        this.email = email;
    }

    // Возвращает возраст пользователя.
        public Integer getAge() {
        return age;
    }

    // Устанавливает возраст пользователя.
    public void setAge(Integer age) {
        this.age = age;
    }
}