package ru.zeker.authentication.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AccountResponse {
    protected UUID id;
    protected String email;
}
