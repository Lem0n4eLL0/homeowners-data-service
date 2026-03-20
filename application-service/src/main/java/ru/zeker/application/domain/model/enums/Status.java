package ru.zeker.application.domain.model.enums;

import lombok.Getter;

@Getter
public enum Status {
    PENDING("На рассмотрении"),
    ACCEPTED("Принята"),
    IN_PROGRESS("В процессе"),
    COMPLETED("Завершено"),
    CANCELLED("Отклонена");

    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }
}
