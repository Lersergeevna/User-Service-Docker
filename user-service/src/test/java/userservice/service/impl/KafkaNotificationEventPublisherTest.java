package userservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import userservice.event.UserNotificationEvent;
import userservice.event.UserOperation;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Проверяет Kafka publisher без настоящей Kafka.
 */
class KafkaNotificationEventPublisherTest {

    @Test
    void publish_shouldSendJsonRecordToKafka() {
        @SuppressWarnings("unchecked")
        Producer<String, String> producer = mock(Producer.class);

        Future<RecordMetadata> future = CompletableFuture.completedFuture(null);
        when(producer.send(any(ProducerRecord.class))).thenReturn(future);

        KafkaNotificationEventPublisher publisher = new KafkaNotificationEventPublisher(
                producer,
                new ObjectMapper(),
                "user-notifications"
        );

        publisher.publish(new UserNotificationEvent(UserOperation.CREATED, "alice@example.com"));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<ProducerRecord<String, String>> recordCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(producer).send(recordCaptor.capture());

        ProducerRecord<String, String> record = recordCaptor.getValue();
        assertEquals("user-notifications", record.topic());
        assertEquals("alice@example.com", record.key());
        assertTrue(record.value().contains("\"operation\":\"CREATED\""));
        assertTrue(record.value().contains("\"email\":\"alice@example.com\""));
    }
}
