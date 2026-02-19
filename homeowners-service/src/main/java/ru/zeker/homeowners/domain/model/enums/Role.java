package ru.zeker.homeowners.domain.model.enums;

import lombok.Getter;

@Getter
public enum Role {
    OWNER("Владелец"),
    CO_OWNER("Совладелец"),
    FAMILY_MEMBER("Член семьи"),
    TENANT("Арендатор");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

}