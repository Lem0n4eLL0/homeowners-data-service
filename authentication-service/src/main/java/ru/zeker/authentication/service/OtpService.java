package ru.zeker.authentication.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import ru.zeker.authentication.util.HmacUtils;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static ru.zeker.common.util.MaskPhoneUtils.maskPhone;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {

    private static final Duration TTL = Duration.ofMinutes(5);
    private static final Duration RESEND_COOLDOWN = Duration.ofSeconds(60);
    private static final String OTP_PREFIX = "otp:";
    private static final String OTP_COOLDOWN_PREFIX = "otp_cooldown:";

    private final RedisTemplate<String, String> redisTemplate;

    private final String otpHmacSecret;

    public String generateAndStore(String phone) {
        var code = generateCode();
        var hmac = HmacUtils.hmacSha256(code, otpHmacSecret);

        redisTemplate.opsForValue()
                .set(OTP_PREFIX + phone, hmac, TTL);

        redisTemplate.opsForValue()
                .set(OTP_COOLDOWN_PREFIX + phone, "locked", RESEND_COOLDOWN);

        log.debug("OTP generated and stored for phone={}, ttl={} seconds",
                maskPhone(phone), TTL.getSeconds());

        return code;
    }

    public void verify(String phone, String code) {
        var key = OTP_PREFIX + phone;

        log.debug("Verifying OTP for phone={}", maskPhone(phone));

        var storedHmac = Optional.ofNullable(redisTemplate.opsForValue().get(key))
                .orElseThrow(() -> {
                    log.debug("OTP not found or expired for phone={}", maskPhone(phone));
                    return new BadCredentialsException("Invalid or expired code");
                });

        var providedHmac = HmacUtils.hmacSha256(code, otpHmacSecret);

        if (!HmacUtils.equals(providedHmac, storedHmac)) {
            log.debug("OTP mismatch for phone={}", maskPhone(phone));
            throw new BadCredentialsException("Invalid or expired code");
        }

        redisTemplate.delete(key);
        redisTemplate.delete(OTP_COOLDOWN_PREFIX + phone);

        log.debug("OTP successfully verified and removed for phone={}", maskPhone(phone));
    }

    public boolean canSend(String phone) {
        return !Boolean.TRUE.equals(redisTemplate.hasKey(OTP_COOLDOWN_PREFIX + phone));
    }

    private String generateCode() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 999999));
    }
}
