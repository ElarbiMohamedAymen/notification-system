package com.reservation.event;

import com.reservation.dto.SlotDto;

public record SlotEvent(SlotEventType type, SlotDto slot) {
}
