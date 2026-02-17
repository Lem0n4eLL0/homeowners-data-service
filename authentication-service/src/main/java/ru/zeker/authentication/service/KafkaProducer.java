package ru.zeker.authentication.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.zeker.common.dto.kafka.sms.SmsEvent;
import ru.zeker.common.dto.kafka.smtp.EmailEvent;

@Service
@RequiredArgsConstructor
@Validated
public class KafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendEmailEvent(@Valid EmailEvent event) {
        kafkaTemplate.send("notification.smtp.verification", event.getEmail(), event);
    }

    public void sendSmsEvent(@Valid SmsEvent event) {
        kafkaTemplate.send("notification.sms.verification", event.getPhone(), event);
    }

}
