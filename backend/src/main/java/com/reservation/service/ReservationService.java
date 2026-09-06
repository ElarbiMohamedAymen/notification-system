package com.reservation.service;

import com.reservation.domain.Slot;
import com.reservation.dto.SlotDto;
import com.reservation.exception.SlotNotAvailableException;
import com.reservation.exception.SlotNotFoundException;
import com.reservation.repository.SlotRepository;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationService {

    private final SlotRepository slotRepository;
    private final NotificationService notificationService;

    public ReservationService(SlotRepository slotRepository, NotificationService notificationService) {
        this.slotRepository = slotRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public SlotDto reserve(Long slotId, String userId) {
        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new SlotNotFoundException(slotId));

        try {
            slot.reserveFor(userId);
            slot = slotRepository.saveAndFlush(slot);
        } catch (IllegalStateException | ObjectOptimisticLockingFailureException e) {
            // Either already RESERVED in memory, or another transaction won
            // the race between our read and our write (version mismatch).
            throw new SlotNotAvailableException(slotId);
        }

        SlotDto dto = SlotDto.from(slot);
        notificationService.slotReserved(dto);
        return dto;
    }
}
