package com.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record CreateSlotRequest(
        @NotBlank String topicName,
        @NotNull Instant startTime,
        @NotNull Instant endTime
) {
}
