package com.reservation.web;

import com.reservation.dto.CreateSlotRequest;
import com.reservation.dto.SlotDto;
import com.reservation.service.SlotService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/slots")
public class AdminSlotController {

    private final SlotService slotService;

    public AdminSlotController(SlotService slotService) {
        this.slotService = slotService;
    }

    @PostMapping
    public ResponseEntity<SlotDto> publishSlot(@Valid @RequestBody CreateSlotRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(slotService.publishSlot(request));
    }
}
