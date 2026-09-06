package com.reservation.dto;

import jakarta.validation.constraints.NotBlank;

public record SubscribeRequest(@NotBlank String userId) {
}
