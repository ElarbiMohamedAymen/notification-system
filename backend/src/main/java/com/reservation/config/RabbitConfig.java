package com.reservation.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String SLOT_EVENTS_EXCHANGE = "slot.events";

    @Bean
    public TopicExchange slotEventsExchange() {
        // A topic exchange (not fanout) so a future consumer that only
        // cares about specific cities can bind with a routing pattern
        // instead of receiving every city's events.
        return new TopicExchange(SLOT_EVENTS_EXCHANGE);
    }

    @Bean
    public Queue slotEventsQueue() {
        // Anonymous, auto-delete queue: every running instance gets its own,
        // so every instance receives every event and can rebroadcast to the
        // WebSocket clients connected to *it*. A shared/durable queue would
        // instead load-balance messages across instances.
        return new AnonymousQueue();
    }

    @Bean
    public Binding slotEventsBinding(Queue slotEventsQueue, TopicExchange slotEventsExchange) {
        // "#" matches every routing key (every city) - this instance's
        // bridge listener needs all of them, not just one city.
        return BindingBuilder.bind(slotEventsQueue).to(slotEventsExchange).with("#");
    }

    @Bean
    public MessageConverter messageConverter() {
        // Jackson-3-based converter (Boot 4's default Jackson stack), not
        // the older Jackson-2-based Jackson2JsonMessageConverter.
        return new JacksonJsonMessageConverter("com.reservation.event", "com.reservation.dto");
    }
}
