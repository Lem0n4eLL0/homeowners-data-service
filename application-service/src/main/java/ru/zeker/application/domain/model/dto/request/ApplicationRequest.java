package ru.zeker.application.domain.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.zeker.application.domain.model.enums.Status;

import java.util.UUID;

import lombok.Builder;
import lombok.Builder.Default;

@Builder
public record ApplicationRequest(
        @NotNull(message = "Property ID is required")
        UUID propertyId,
        @NotNull(message = "Property ID is required")
        UUID accountId,
        @NotBlank(message = "Title is required")
        @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
        String title,
        @Size(max = 1000, message = "Comment must not exceed 1000 characters")
        String comment,
        Status status

) {
    public ApplicationRequest {
        if (status == null) {
            status = Status.PENDING;
        }
    }
}
