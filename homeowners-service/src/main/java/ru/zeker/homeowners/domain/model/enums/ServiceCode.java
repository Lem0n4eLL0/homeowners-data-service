package ru.zeker.homeowners.domain.model.enums;


import lombok.Getter;

@Getter
public enum ServiceCode {
    ELECTRICITY("Электричество"),
    GAS("Газ"),
    COLD_WATER("Холодная вода"),
    HOT_WATER("Горячая вода"),
    TRASH("Вывоз мусора"),
    MANAGEMENT("Управляющая компания");

    private final String displayName;

    ServiceCode(String displayName) {
        this.displayName = displayName;
    }

}
