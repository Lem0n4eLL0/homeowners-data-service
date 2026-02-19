package ru.zeker.notification.service.handlers.sms;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.dezhik.sms.sender.SenderService;
import ru.dezhik.sms.sender.api.InvocationStatus;
import ru.dezhik.sms.sender.api.smsru.SMSRuResponseStatus;
import ru.dezhik.sms.sender.api.smsru.send.SMSRuSendRequest;
import ru.dezhik.sms.sender.api.smsru.send.SMSRuSendResponse;
import ru.zeker.common.dto.kafka.sms.SmsEvent;
import ru.zeker.notification.service.handlers.KafkaEventHandler;

import java.util.Arrays;
import java.util.Collections;

import static ru.zeker.common.util.MaskPhoneUtils.maskPhone;

@Service
@Slf4j
@RequiredArgsConstructor
public class VerificationSmsHandler implements KafkaEventHandler<SmsEvent> {

    private final SenderService senderService;

    @Override
    public Class<SmsEvent> getEventType() {
        return SmsEvent.class;
    }

    @Override
    public void handle(SmsEvent event) {
        String phone = event.getPhone();
        String maskedPhone = maskPhone(phone);
        String code = event.getCode();
        String text = String.format("Your verification code: %s", code);

        log.info("Sending SMS to {}", maskedPhone);

        SMSRuSendRequest sendRequest = new SMSRuSendRequest();
        sendRequest.setReceivers(Collections.singleton(phone));
        sendRequest.setText(text);

        try {
            SMSRuSendResponse sendResponse = senderService.execute(sendRequest);

            // Если сам запрос успешно ушёл на сервер SMS.ru
            if (sendRequest.getStatus() != InvocationStatus.SUCCESS) {
                log.error("SMS request execution FAILED for {}. Status: {}, Exception: {}",
                        maskedPhone,
                        sendRequest.getStatus(),
                        sendRequest.getException() != null ? sendRequest.getException().getMessage() : "none"
                );
            } else {
                // API вызов прошёл, проверяем статус ответа
                if (sendResponse.getResponseStatus() == SMSRuResponseStatus.IN_QUEUE) {
                    log.info("SMS sent successfully to {}. Balance: {}, SMS IDs: {}",
                            maskedPhone,
                            sendResponse.getBalance(),
                            Arrays.toString(sendResponse.getMsgIds().toArray())
                    );
                } else {
                    log.error("SMS sending FAILED for {}. Response status: {}, Balance: {}, Message IDs: {}",
                            maskedPhone,
                            sendResponse.getResponseStatus(),
                            sendResponse.getBalance(),
                            sendResponse.getMsgIds() != null
                                    ? Arrays.toString(sendResponse.getMsgIds().toArray())
                                    : "[]"
                    );
                }
            }

        } catch (Exception e) {
            log.error("Unexpected exception while sending SMS to {}: {}", maskedPhone, e.getMessage(), e);
            throw e;
        }
    }
}
