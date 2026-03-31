package ru.zeker.homeowners.domain.model.enums;


import lombok.Getter;

@Getter
public enum ServiceCode {
  ELECTRICITY("Электричество", true),
  GAS("Газ", true),
  COLD_WATER("Холодная вода", true),
  HOT_WATER("Горячая вода", true),
  TRASH("Вывоз мусора", false),
  HEATING("Отопление", true),
  MANAGEMENT("Управляющая компания",false);

  private final String displayName;
  private final boolean isVisible;


  ServiceCode(String displayName, boolean isVisible) {
    this.displayName = displayName;
    this.isVisible = isVisible;
  }

}
