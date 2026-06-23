# User Service

Spring Boot REST API для управления пользователями.

Проект реализует CRUD-операции для пользователей, хранит данные в PostgreSQL, использует Flyway для миграций, публикует Kafka-события при создании и удалении пользователя, а также содержит Swagger/OpenAPI-документацию и HATEOAS-ссылки для навигации по API.

## Основная функциональность

* создание пользователя;
* получение пользователя по id;
* получение списка пользователей;
* обновление пользователя;
* удаление пользователя;
* валидация входных данных;
* обработка ошибок через единый exception handler;
* хранение данных в PostgreSQL;
* миграции базы данных через Flyway;
* публикация Kafka-событий при создании и удалении пользователя;
* Swagger-документация API;
* HATEOAS-ссылки в ответах API.

## Технологии

* Java 21
* Spring Boot 3.5.0
* Spring Web
* Spring Data JPA
* Spring Validation
* Spring Kafka
* Spring HATEOAS
* Springdoc OpenAPI
* PostgreSQL
* Flyway
* Maven
* Lombok
* JUnit 5
* MockMvc
* H2 для тестов
* Docker / Docker Compose

## Структура проекта

user-service/
├── src/main/java/userservice/
│   ├── assembler/          # HATEOAS assembler
│   ├── config/             # OpenAPI configuration
│   ├── constants/          # константы сообщений и Swagger-документации
│   ├── controller/         # REST controller
│   ├── dto/                # DTO request/response
│   ├── entity/             # JPA entities
│   ├── event/              # Kafka events
│   ├── exception/          # custom exceptions и global exception handler
│   ├── mapper/             # преобразование entity <-> DTO
│   ├── repository/         # Spring Data JPA repositories
│   └── service/            # бизнес-логика и Kafka publisher
├── src/main/resources/
│   ├── db/migration/       # Flyway migrations
│   ├── application.yml
│   └── logback.xml
└── src/test/
    ├── java/               # integration/unit tests
    └── resources/

## HATEOAS

В ответы API добавлены HATEOAS-ссылки.

Для одного пользователя возвращаются ссылки:

* `self` — ссылка на текущего пользователя;
* `users` — ссылка на список пользователей;
* `update-user` — ссылка для обновления пользователя;
* `delete-user` — ссылка для удаления пользователя.

Для коллекции пользователей возвращаются ссылки:

* `self` — ссылка на список пользователей;
* `create-user` — ссылка для создания пользователя.

## Swagger / OpenAPI

В проект добавлена Swagger-документация через Springdoc OpenAPI.

После запуска приложения Swagger UI доступен по адресу:

http://localhost:8080/swagger-ui/index.html

OpenAPI JSON доступен по адресу:

http://localhost:8080/v3/api-docs


## Безопасность

В API наружу возвращаются только DTO-ответы и HATEOAS-ссылки.

В текущей учебной версии API не содержит авторизацию и роли. Для production-версии нужно дополнительно добавить Spring Security, разграничение доступа, пагинацию и отключение Swagger UI в production-профиле.
