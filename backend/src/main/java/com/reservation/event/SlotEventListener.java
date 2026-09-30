package com.reservation.event;

import com.reservation.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class SlotEventListener {

    private final NotificationService notificationService;

    public SlotEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // Listens on this instance's own anonymous, auto-delete queue (declared
    // in RabbitConfig, bound to the exchange with "#"), so every instance
    // gets every event and rebroadcasts to the WebSocket clients connected
    // to it. The queue disappears when this instance shuts down.
    @RabbitListener(queues = "#{slotEventsQueue.name}")
    public void onSlotEvent(SlotEvent event) {
        switch (event.type()) {
            case PUBLISHED -> notificationService.slotPublished(event.slot());
            case RESERVED -> notificationService.slotReserved(event.slot());
        }
    }
}
