package ru.zeker.common.util;

import lombok.experimental.UtilityClass;

import java.util.Objects;

@UtilityClass
public class MaskPhoneUtils {

    public static String maskPhone(String phone) {
        if (Objects.isNull(phone) || phone.length() < 4) return "****";
        return phone.substring(0, phone.length() - 4).replaceAll("\\d", "*")
                + phone.substring(phone.length() - 4);
    }
}
