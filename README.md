# user-service

Консольное Java-приложение без Spring для CRUD-операций над сущностью `User` с использованием Hibernate ORM и PostgreSQL.

## Что реализовано

- Hibernate ORM без Spring
- PostgreSQL как основная БД
- Запуск PostgreSQL через Docker Compose
- Конфигурация Hibernate через `hibernate.cfg.xml`
- DAO + Service + UI слои
- CRUD для `User`: create, read, update, delete
- Flyway-миграции
- Логирование через SLF4J + Logback
- Транзакции для операций записи
- Обработка пользовательских, сервисных и database-ошибок
- Maven для сборки и зависимостей
- Unit-тесты service-слоя

## Структура

```text
src/main/java/userservice
├── config      # Hibernate, Flyway, настройки подключения
├── dao         # DAO-интерфейс и Hibernate-реализация
├── dto         # DTO для создания и обновления пользователя
├── entity      # Hibernate entity
├── exception   # пользовательские исключения
├── mapper      # преобразование DTO <-> entity
├── service     # бизнес-логика
├── ui          # консольное меню и вывод
└── util        # валидация, чтение ввода, работа с исключениями
```

## Требования

- Java 21+
- Maven
- Docker Desktop, если PostgreSQL запускается через Docker

## Быстрый запуск PostgreSQL через Docker

Из корня проекта:

```bash
docker compose up -d
```

Контейнер создаёт PostgreSQL со значениями по умолчанию:

```text
DB_URL=jdbc:postgresql://localhost:5432/user_service_db
DB_USERNAME=postgres
DB_PASSWORD=postgres
```

Проверить контейнер:

```bash
docker ps
```

Остановить контейнер:

```bash
docker compose down
```

Остановить контейнер и удалить данные БД:

```bash
docker compose down -v
```

## Запуск приложения

### Git Bash / Linux / macOS

```bash
DB_URL="jdbc:postgresql://localhost:5432/user_service_db" \
DB_USERNAME="postgres" \
DB_PASSWORD="postgres" \
mvn clean test exec:java
```

Или, если используются значения по умолчанию из `AppProperties`:

```bash
mvn clean test exec:java
```

### Windows PowerShell

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/user_service_db"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="postgres"
mvn clean test exec:java
```

## Ручное создание БД без Docker

Если PostgreSQL установлен локально без Docker:

```sql
CREATE DATABASE user_service_db;
```

После этого передай свои значения подключения через переменные окружения или системные свойства.

## Системные свойства вместо переменных окружения

```bash
mvn exec:java \
  -Ddb.url="jdbc:postgresql://localhost:5432/user_service_db" \
  -Ddb.username="postgres" \
  -Ddb.password="postgres"
```

## Сущность User

Таблица `users` создаётся Flyway-миграцией:

```text
id         bigserial primary key
name       varchar(100) not null
email      varchar(150) not null unique
age        integer not null check 1..130
created_at timestamp not null
```

## Замечания по безопасности и устойчивости

- Spring не используется.
- SQL строится через Hibernate и bind-параметры, пользовательский ввод не склеивается с SQL строками.
- SQL и bind-параметры не логируются по умолчанию.
- Пароль БД не хранится в бизнес-коде; его можно передать через env/system properties.
- Технические детали ошибок пишутся в лог, а пользователю показываются безопасные сообщения.
- `Session` закрываются через try-with-resources.
- `SessionFactory` создаётся один раз и закрывается при завершении приложения.
- Уникальность e-mail проверяется и в сервисе, и на уровне БД через unique constraint.
- Валидация Java-кода согласована с SQL-ограничениями.
