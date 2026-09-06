package com.reservation.web;

import com.reservation.dto.ReserveRequest;
import com.reservation.dto.SlotDto;
import com.reservation.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/slots")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/{slotId}/reserve")
    public ResponseEntity<SlotDto> reserve(@PathVariable Long slotId, @Valid @RequestBody ReserveRequest request) {
        return ResponseEntity.ok(reservationService.reserve(slotId, request.userId()));
    }
}
