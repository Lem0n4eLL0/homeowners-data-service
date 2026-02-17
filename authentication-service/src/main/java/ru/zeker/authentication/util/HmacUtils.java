package ru.zeker.authentication.util;

import lombok.experimental.UtilityClass;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

@UtilityClass
public class HmacUtils {

    public static String hmacSha256(String data, String secretKey) {
        try {
            var mac = Mac.getInstance("HmacSHA256");
            var keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            var hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate HMAC", e);
        }
    }

    public static boolean equals(String hmac1, String hmac2) {
        if (Objects.isNull(hmac1) || Objects.isNull(hmac2) || hmac1.length() != hmac2.length()) {
            return false;
        }
        var result = 0;
        for (var i = 0; i < hmac1.length(); i++) {
            result |= hmac1.charAt(i) ^ hmac2.charAt(i);
        }
        return result == 0;
    }
}
