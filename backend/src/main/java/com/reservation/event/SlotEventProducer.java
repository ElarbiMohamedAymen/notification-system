package com.reservation.event;

import com.reservation.config.RabbitConfig;
import com.reservation.dto.SlotDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class SlotEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public SlotEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void slotPublished(SlotDto slot) {
        send(new SlotEvent(SlotEventType.PUBLISHED, slot));
    }

    public void slotReserved(SlotDto slot) {
        send(new SlotEvent(SlotEventType.RESERVED, slot));
    }

    private void send(SlotEvent event) {
        // Routing key is the city name: today every instance binds with "#"
        // and gets everything, but this lets a future city-scoped consumer
        // bind to just the cities it cares about.
        rabbitTemplate.convertAndSend(RabbitConfig.SLOT_EVENTS_EXCHANGE, event.slot().cityName(), event);
    }
}
