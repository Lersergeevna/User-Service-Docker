package userservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import userservice.config.AppProperties;
import userservice.constants.Messages;
import userservice.event.UserNotificationEvent;
import userservice.exception.InvalidInputException;
import userservice.exception.NotificationEventPublishException;
import userservice.service.NotificationEventPublisher;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Отправляет события уведомлений в Kafka topic для notification-service.
 */
public class KafkaNotificationEventPublisher implements NotificationEventPublisher {
    private static final long SEND_TIMEOUT_SECONDS = 5;

    private final Producer<String, String> producer;
    private final ObjectMapper objectMapper;
    private final String topic;

    /**
     * Создаёт Kafka publisher с настройками из {@link AppProperties}.
     */
    public KafkaNotificationEventPublisher() {
        this(
                createProducer(AppProperties.kafkaBootstrapServers()),
                new ObjectMapper(),
                AppProperties.kafkaUserNotificationsTopic()
        );
    }

    /**
     * Создаёт Kafka publisher с явно переданными зависимостями.
     * Конструктор используется в тестах и при ручной настройке publisher.
     *
     * @param producer Kafka producer
     * @param objectMapper JSON mapper для сериализации события
     * @param topic Kafka topic для событий уведомлений
     */
    public KafkaNotificationEventPublisher(
            @NonNull Producer<String, String> producer,
            @NonNull ObjectMapper objectMapper,
            @NonNull String topic
    ) {
        this.producer = producer;
        this.objectMapper = objectMapper;
        this.topic = topic;
    }

    /**
     * Проверяет событие, сериализует его в JSON и отправляет в Kafka.
     * В качестве ключа сообщения используется e-mail пользователя.
     *
     * @param event событие уведомления о создании или удалении пользователя
     * @throws InvalidInputException если событие пустое или содержит некорректные данные
     * @throws NotificationEventPublishException если событие не удалось сериализовать или отправить в Kafka
     */
    @Override
    public void publish(UserNotificationEvent event) {
        if (event == null || event.operation() == null || event.email() == null || event.email().isBlank()) {
            throw new InvalidInputException(Messages.NULL_NOTIFICATION_EVENT);
        }

        try {
            String payload = objectMapper.writeValueAsString(event);
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, event.email(), payload);

            producer.send(record).get(SEND_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (JsonProcessingException | InterruptedException | ExecutionException | TimeoutException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new NotificationEventPublishException(Messages.NOTIFICATION_EVENT_PUBLISH_FAILED, e);
        }
    }

    /**
     * Закрывает Kafka producer и освобождает сетевые ресурсы.
     */
    @Override
    public void close() {
        producer.close();
    }

    /**
     * Создаёт Kafka producer со строковой сериализацией ключа и значения.
     *
     * @param bootstrapServers адрес Kafka bootstrap servers
     * @return настроенный Kafka producer
     */
    private static Producer<String, String> createProducer(String bootstrapServers) {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, "user-service-producer");
        properties.put(ProducerConfig.ACKS_CONFIG, "all");

        return new KafkaProducer<>(properties);
    }
}
