package com.reservation.dto;

import jakarta.validation.constraints.NotBlank;

public record ReserveRequest(@NotBlank String userId) {
}
