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
        messagingTemplate.convertAndSend(destinationFor(slot.topicName()), slot);
    }

    public void slotReserved(SlotDto slot) {
        // Broadcast on the same topic so every other subscriber still holding
        // this slot in their UI removes it immediately, without polling.
        messagingTemplate.convertAndSend(destinationFor(slot.topicName()), slot);
    }

    private String destinationFor(String topicName) {
        return "/topic/" + topicName;
    }
}
