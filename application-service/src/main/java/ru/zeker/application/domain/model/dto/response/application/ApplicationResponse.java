package ru.zeker.application.domain.model.dto.response.application;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.zeker.application.domain.model.enums.Status;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public record ApplicationResponse (
        String title,
        Status status
){}
