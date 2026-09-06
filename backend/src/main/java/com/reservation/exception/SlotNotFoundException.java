package com.reservation.exception;

public class SlotNotFoundException extends RuntimeException {

    public SlotNotFoundException(Long slotId) {
        super("Slot " + slotId + " not found");
    }
}
