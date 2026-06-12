package userservice.config;

/**
 * Предоставляет настройки приложения из системных свойств или переменных окружения.
 */
public final class AppProperties {
    private static final String DB_URL_PROPERTY = "db.url";
    private static final String DB_URL_ENV = "DB_URL";

    private static final String DB_USERNAME_PROPERTY = "db.username";
    private static final String DB_USERNAME_ENV = "DB_USERNAME";

    private static final String DB_PASSWORD_PROPERTY = "db.password";
    private static final String DB_PASSWORD_ENV = "DB_PASSWORD";

    private static final String KAFKA_BOOTSTRAP_SERVERS_PROPERTY = "kafka.bootstrap.servers";
    private static final String KAFKA_BOOTSTRAP_SERVERS_ENV = "KAFKA_BOOTSTRAP_SERVERS";

    private static final String KAFKA_USER_NOTIFICATIONS_TOPIC_PROPERTY = "kafka.topic.user.notifications";
    private static final String KAFKA_USER_NOTIFICATIONS_TOPIC_ENV = "KAFKA_USER_NOTIFICATIONS_TOPIC";

    private static final String DEFAULT_DB_URL = "jdbc:postgresql://localhost:5432/user_service_db";
    private static final String DEFAULT_DB_USERNAME = "postgres";
    private static final String DEFAULT_DB_PASSWORD = "postgres";
    private static final String DEFAULT_KAFKA_BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String DEFAULT_KAFKA_USER_NOTIFICATIONS_TOPIC = "user-notifications";

    private AppProperties() {
    }

    /**
     * Возвращает URL подключения к базе данных.
     *
     * @return URL подключения к базе данных
     */
    public static String dbUrl() {
        return read(DB_URL_PROPERTY, DB_URL_ENV, DEFAULT_DB_URL);
    }

    /**
     * Возвращает имя пользователя базы данных.
     *
     * @return имя пользователя базы данных
     */
    public static String dbUsername() {
        return read(DB_USERNAME_PROPERTY, DB_USERNAME_ENV, DEFAULT_DB_USERNAME);
    }

    /**
     * Возвращает пароль базы данных.
     *
     * @return пароль базы данных
     */
    public static String dbPassword() {
        return read(DB_PASSWORD_PROPERTY, DB_PASSWORD_ENV, DEFAULT_DB_PASSWORD);
    }

    /**
     * Возвращает адрес Kafka bootstrap servers.
     *
     * @return адрес Kafka
     */
    public static String kafkaBootstrapServers() {
        return read(
                KAFKA_BOOTSTRAP_SERVERS_PROPERTY,
                KAFKA_BOOTSTRAP_SERVERS_ENV,
                DEFAULT_KAFKA_BOOTSTRAP_SERVERS
        );
    }

    /**
     * Возвращает название Kafka topic для уведомлений пользователей.
     *
     * @return название Kafka topic
     */
    public static String kafkaUserNotificationsTopic() {
        return read(
                KAFKA_USER_NOTIFICATIONS_TOPIC_PROPERTY,
                KAFKA_USER_NOTIFICATIONS_TOPIC_ENV,
                DEFAULT_KAFKA_USER_NOTIFICATIONS_TOPIC
        );
    }

    private static String read(String systemKey, String envKey, String defaultValue) {
        String systemValue = System.getProperty(systemKey);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }

        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        return defaultValue;
    }
}