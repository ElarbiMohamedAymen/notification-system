import { useEffect, useState } from "react";
import * as api from "./api";
import type { SlotDto } from "./types";
import { useTopicNotifications } from "./useTopicNotifications";

export default function UserPanel({ userId }: { userId: string }) {
  const [topicInput, setTopicInput] = useState("station-42");
  const [subscribedTopics, setSubscribedTopics] = useState<string[]>([]);
  const [slots, setSlots] = useState<Record<number, SlotDto>>({});
  const [message, setMessage] = useState<string | null>(null);

  useEffect(() => {
    api.mySubscriptions(userId).then(setSubscribedTopics).catch(() => {});
  }, [userId]);

  useTopicNotifications(subscribedTopics, (slot) => {
    setSlots((prev) => {
      const next = { ...prev };
      if (slot.status === "AVAILABLE") {
        next[slot.id] = slot;
      } else {
        delete next[slot.id];
      }
      return next;
    });
  });

  async function handleSubscribe() {
    const topic = topicInput.trim();
    if (!topic || subscribedTopics.includes(topic)) return;
    await api.subscribeTopic(userId, topic);
    setSubscribedTopics((prev) => [...prev, topic]);

    const existing = await api.availableSlots(topic);
    setSlots((prev) => {
      const next = { ...prev };
      for (const slot of existing) next[slot.id] = slot;
      return next;
    });
  }

  async function handleUnsubscribe(topic: string) {
    await api.unsubscribeTopic(userId, topic);
    setSubscribedTopics((prev) => prev.filter((t) => t !== topic));
    setSlots((prev) => {
      const next = { ...prev };
      for (const [id, slot] of Object.entries(next)) {
        if (slot.topicName === topic) delete next[Number(id)];
      }
      return next;
    });
  }

  async function handleReserve(slot: SlotDto) {
    setMessage(null);
    try {
      await api.reserveSlot(slot.id, userId);
      setSlots((prev) => {
        const next = { ...prev };
        delete next[slot.id];
        return next;
      });
      setMessage(`Reserved slot #${slot.id}`);
    } catch (e) {
      setMessage(e instanceof Error ? e.message : "Reservation failed");
    }
  }

  const visibleSlots = Object.values(slots).sort((a, b) => a.startTime.localeCompare(b.startTime));

  return (
    <div>
      <h2>Subscribe to a topic</h2>
      <div className="row">
        <input
          value={topicInput}
          onChange={(e) => setTopicInput(e.target.value)}
          placeholder="e.g. station-42"
        />
        <button onClick={handleSubscribe}>Subscribe</button>
      </div>

      <h3>Your subscriptions</h3>
      {subscribedTopics.length === 0 && <p className="muted">No subscriptions yet.</p>}
      <ul>
        {subscribedTopics.map((topic) => (
          <li key={topic}>
            {topic} <button onClick={() => handleUnsubscribe(topic)}>Unsubscribe</button>
          </li>
        ))}
      </ul>

      <h3>Available slots (pushed live, no polling)</h3>
      {message && <p className="message">{message}</p>}
      {visibleSlots.length === 0 && <p className="muted">Waiting for a slot notification...</p>}
      <ul>
        {visibleSlots.map((slot) => (
          <li key={slot.id}>
            [{slot.topicName}] {new Date(slot.startTime).toLocaleString()} -{" "}
            {new Date(slot.endTime).toLocaleString()}{" "}
            <button onClick={() => handleReserve(slot)}>Reserve</button>
          </li>
        ))}
      </ul>
    </div>
  );
}
