package ru.zeker.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Service for listening to and processing Kafka events.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConsumerKafkaListeners {
    private final KafkaEventDispatcher dispatcher;

    @KafkaListener(
            topics = "notification.sms.verification",
            groupId = "notification-sms-group",
            containerFactory = "smsKafkaListenerContainerFactory"
    )
    public void listenSms(ConsumerRecord<String, Object> record) {
        var event = record.value();
        log.info("Received SMS event: {}", event);
        dispatcher.dispatch(event);
    }

    @KafkaListener(
            topics = "notification.smtp.verification",
            groupId = "notification-smtp-group",
            containerFactory = "smtpKafkaListenerContainerFactory"
    )
    public void listenSmtp(ConsumerRecord<String, Object> record) {
        var event = record.value();
        log.info("Received SMTP event: {}", event);
        dispatcher.dispatch(event);
    }


}
