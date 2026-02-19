package ru.zeker.homeowners.domain.model.enums;

import lombok.Getter;

@Getter
public enum MeterType {
    COLD_WATER("Холодная вода"),
    HOT_WATER("Горячая вода"),
    ELECTRICITY("Электричество"),
    GAS("Газ"),
    HEATING("Отопление");

    private final String displayName;

    MeterType(String displayName) {
        this.displayName = displayName;
    }

}