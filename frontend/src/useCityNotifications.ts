import { useEffect, useRef } from "react";
import type { StompSubscription } from "@stomp/stompjs";
import { getStompClient } from "./stompClient";
import type { SlotDto } from "./types";

/**
 * Subscribes to /topic/{cityName} (STOMP's own "/topic/" destination
 * convention, unrelated to our domain City) for each given city and invokes
 * onSlotEvent for every push the server sends (new slot published, or a
 * slot elsewhere getting reserved). No polling: the callback only fires
 * when the server actually has something to say.
 */
export function useCityNotifications(cities: string[], onSlotEvent: (slot: SlotDto) => void) {
  const callbackRef = useRef(onSlotEvent);
  callbackRef.current = onSlotEvent;

  useEffect(() => {
    const client = getStompClient();
    const subscriptions = new Map<string, StompSubscription>();

    const subscribeAll = () => {
      for (const city of cities) {
        if (subscriptions.has(city)) continue;
        const sub = client.subscribe(`/topic/${city}`, (message) => {
          callbackRef.current(JSON.parse(message.body) as SlotDto);
        });
        subscriptions.set(city, sub);
      }
    };

    if (client.connected) {
      subscribeAll();
    }
    client.onConnect = subscribeAll;

    return () => {
      subscriptions.forEach((sub) => sub.unsubscribe());
      subscriptions.clear();
    };
  }, [cities.join(",")]);
}
