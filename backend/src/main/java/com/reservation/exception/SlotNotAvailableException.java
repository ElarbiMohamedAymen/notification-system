package com.reservation.exception;

public class SlotNotAvailableException extends RuntimeException {

    public SlotNotAvailableException(Long slotId) {
        super("Slot " + slotId + " is no longer available");
    }
}
