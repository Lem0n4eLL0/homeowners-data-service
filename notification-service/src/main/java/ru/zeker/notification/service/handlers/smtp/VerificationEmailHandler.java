package ru.zeker.notification.service.handlers.smtp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.zeker.common.dto.kafka.smtp.EmailEvent;
import ru.zeker.notification.service.EmailService;
import ru.zeker.notification.service.handlers.KafkaEventHandler;
import ru.zeker.notification.util.ThymeleafUtils;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class VerificationEmailHandler implements KafkaEventHandler<EmailEvent> {
    private static final String EMAIL_VERIFICATION_TEMPLATE = "email/emailVerification.html";

    private final EmailService emailService;

    @Value("${app.frontend-url}")
    private String applicationUrl;

    @Value("${app.links.email-verification}")
    private String emailVerificationUrl;

    @Override
    public Class<EmailEvent> getEventType() {
        return EmailEvent.class;
    }

    @Override
    public void handle(EmailEvent event) {
        log.debug("Setting up the context of the registration confirmation email: {}",
                event.getEmail());

        var verificationUrl = applicationUrl + emailVerificationUrl + "?token=" + event.getToken();

        var context = emailService.createEmailContext(
                event,
                "Подтверждение почты",
                EMAIL_VERIFICATION_TEMPLATE,
                Map.of(ThymeleafUtils.ACTION_URL, verificationUrl)
        );

        emailService.sendEmail(context);
    }

}
