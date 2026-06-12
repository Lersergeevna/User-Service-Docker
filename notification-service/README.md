# notification-service

Spring Boot микросервис для отправки email-уведомлений при создании или удалении пользователя.

## Что делает сервис

- получает события из Kafka topic `user-notifications`;
- отправляет email пользователю в зависимости от операции;
- предоставляет REST API для ручной отправки уведомления;
- хранит статусы отправки уведомлений о создании пользователя;
- повторно отправляет неотправленные CREATED-уведомления через scheduler;
- использует custom exceptions и единый `GlobalExceptionHandler`;
- хранит пользовательские сообщения и тексты писем в `Messages`;
- содержит интеграционные тесты отправки email через GreenMail.

## Kafka-сообщение

```json
{
  "operation": "CREATED",
  "email": "user@example.com"
}
```

Поддерживаемые операции:

- `CREATED`
- `DELETED`

## Тексты писем

Создание пользователя:

```text
Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.
```

Удаление пользователя:

```text
Здравствуйте! Ваш аккаунт был удалён.
```

## REST API

```http
POST /api/v1/notifications
Content-Type: application/json
```

Пример:

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

## Запуск

В составе всей системы сервис запускается из корня проекта:

```bash
docker compose up --build
```

Локально только модуль можно запустить так:

```bash
mvn spring-boot:run
```

Для локального запуска нужны PostgreSQL, Kafka и SMTP-сервер MailHog.

## Запуск тестов

Из корня проекта:

```bash
mvn -pl notification-service test
```

Из папки модуля:

```bash
mvn clean test
```
