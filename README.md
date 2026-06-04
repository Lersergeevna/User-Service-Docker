# User Service

REST API для управления пользователями.

Проект реализован на Spring Boot.  
Позволяет создавать, получать, обновлять и удалять пользователей.

## Стек

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- PostgreSQL
- Flyway
- JUnit 5
- MockMvc
- Maven
- Docker Compose

## Функциональность

API поддерживает:

- создание пользователя;
- получение пользователя по id;
- получение списка пользователей;
- обновление пользователя;
- удаление пользователя.

Контроллеры не возвращают Entity напрямую.  
Для входящих и исходящих данных используются DTO.

## Структура проекта

```text
src/main/java/.../userservice
├── controller   # REST-контроллеры
├── dto          # DTO для запросов и ответов
├── entity       # JPA-сущности
├── exception    # обработка ошибок
├── mapper       # преобразование Entity <-> DTO
├── repository   # Spring Data JPA репозитории
├── service      # бизнес-логика
└── constants    # текстовые сообщения