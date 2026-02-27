package ru.zeker.application.domain.model.enums;

import lombok.Getter;

@Getter
public enum Status {
    NEW("Новая"),
    ACCEPTED("Принята"),
    AT_WORK("В работе"),
    DONE("Выполнена");

    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }
}
