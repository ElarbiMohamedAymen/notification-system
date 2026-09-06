package com.reservation.dto;

import com.reservation.domain.Slot;

import java.time.Instant;

public record SlotDto(
        Long id,
        String topicName,
        Instant startTime,
        Instant endTime,
        String status,
        String reservedBy
) {

    public static SlotDto from(Slot slot) {
        return new SlotDto(
                slot.getId(),
                slot.getTopic().getName(),
                slot.getStartTime(),
                slot.getEndTime(),
                slot.getStatus().name(),
                slot.getReservedBy()
        );
    }
}
