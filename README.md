# user-notification-system

Multi-module Maven-проект с двумя сервисами:

```text
user-notification-system/
├── pom.xml
├── docker-compose.yml
├── user-service/
└── notification-service/
```

## Модули

### user-service

Консольное Java/Hibernate-приложение для CRUD-операций над пользователями.
После создания или удаления пользователя отправляет Kafka-событие в topic `user-notifications`.

Событие содержит:

```json
{
  "operation": "CREATED",
  "email": "user@example.com"
}
```

Поддерживаемые операции:

- `CREATED`
- `DELETED`

### notification-service

Spring Boot микросервис, который:

- читает события из Kafka;
- отправляет email через SMTP;
- предоставляет REST API для ручной отправки уведомлений;
- хранит статусы отправки CREATED-уведомлений;
- повторно отправляет неотправленные CREATED-уведомления через scheduler.

## Запуск тестов

Из корня проекта:

```bash
mvn test
```

Отдельно по модулям:

```bash
mvn -pl user-service test
mvn -pl notification-service test
```

## Запуск через Docker Compose

Оба приложения и инфраструктура запускаются из корня проекта:

```bash
docker compose up --build
```

Будут подняты:

- `user-service`
- `notification-service`
- PostgreSQL для user-service
- PostgreSQL для notification-service
- Kafka
- MailHog

MailHog доступен по адресу:

```text
http://localhost:8025
```

## Порты

```text
user-service PostgreSQL:        localhost:5432
notification-service PostgreSQL: localhost:5435
Kafka:                          localhost:9092
MailHog SMTP:                   localhost:1025
MailHog UI:                     localhost:8025
notification-service API:       localhost:8081
```

## REST API notification-service

```http
POST /api/v1/notifications
Content-Type: application/json
```

Пример запроса:

```json
{
  "operation": "CREATED",
  "email": "test@example.com"
}
```

Успешный ответ:

```json
{
  "email": "test@example.com",
  "status": "SENT"
}
```
