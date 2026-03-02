package ru.zeker.homeowners.domain.model.enums;


import lombok.Getter;

@Getter
public enum ServiceCode {
    ELECTRICITY("Электричество"),
    GAS("Газ"),
    WATER("Вода"),
    TRASH("Вывоз мусора");

    private final String displayName;

    ServiceCode(String displayName) {
        this.displayName = displayName;
    }

}
