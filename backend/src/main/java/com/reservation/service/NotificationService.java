package com.reservation.service;

import com.reservation.dto.SlotDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void slotPublished(SlotDto slot) {
        messagingTemplate.convertAndSend(destinationFor(slot.cityName()), slot);
    }

    public void slotReserved(SlotDto slot) {
        // Broadcast on the same city channel so every other subscriber still
        // holding this slot in their UI removes it immediately, without polling.
        messagingTemplate.convertAndSend(destinationFor(slot.cityName()), slot);
    }

    private String destinationFor(String cityName) {
        // STOMP's "/topic/" destination prefix is protocol convention here,
        // not our domain City - it's the broadcast-style STOMP channel type.
        return "/topic/" + cityName;
    }
}
