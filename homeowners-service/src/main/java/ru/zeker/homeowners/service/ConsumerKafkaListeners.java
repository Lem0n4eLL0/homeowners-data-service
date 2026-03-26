package ru.zeker.homeowners.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.zeker.common.dto.kafka.homeowners.AccrualEvent;
import ru.zeker.homeowners.domain.model.entity.Accrual;
import ru.zeker.homeowners.domain.model.entity.PersonalAccount;

/**
 * Service for listening to and processing Kafka events.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConsumerKafkaListeners {

    private final PersonalAccountService personalAccountService;
    private final AccrualService accrualService;

    @KafkaListener(
            topics = "homeowners.accruals",
            containerFactory = "accrualKafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, AccrualEvent> record) {
        try {
            AccrualEvent event = record.value();
            log.info("Received accrual event: {}", event);
            PersonalAccount account = personalAccountService.getByNumberAndCompanyId(
                    event.accountNumber(), event.companyId());
            Accrual accrual = accrualService.createFromEvent(event, account);
            log.info("Accrual processed successfully, accrualId={}", accrual.getId());
        } catch (Exception e) {
            log.error("Accrual event error {}", e.getMessage());
            throw e;
        }
    }

}
