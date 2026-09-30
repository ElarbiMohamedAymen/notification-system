import { useEffect, useState } from "react";
import * as api from "./api";
import type { SlotDto } from "./types";
import { useCityNotifications } from "./useCityNotifications";

const CITY_SUGGESTIONS = ["Paris", "Lyon", "Marseille", "Tunis", "Sfax", "Sousse"];

export default function UserPanel({ userId }: { userId: string }) {
  const [cityInput, setCityInput] = useState("");
  const [myCities, setMyCities] = useState<string[]>([]);
  const [slots, setSlots] = useState<Record<number, SlotDto>>({});
  const [message, setMessage] = useState<string | null>(null);

  useEffect(() => {
    api.mySubscriptions(userId).then(setMyCities).catch(() => {});
  }, [userId]);

  useCityNotifications(myCities, (slot) => {
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

  async function handleAddCity() {
    const city = cityInput.trim();
    if (!city || myCities.includes(city)) return;
    await api.subscribeCity(userId, city);
    setMyCities((prev) => [...prev, city]);
    setCityInput("");

    const existing = await api.availableSlots(city);
    setSlots((prev) => {
      const next = { ...prev };
      for (const slot of existing) next[slot.id] = slot;
      return next;
    });
  }

  async function handleRemoveCity(city: string) {
    await api.unsubscribeCity(userId, city);
    setMyCities((prev) => prev.filter((c) => c !== city));
    setSlots((prev) => {
      const next = { ...prev };
      for (const [id, slot] of Object.entries(next)) {
        if (slot.cityName === city) delete next[Number(id)];
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
      <h2>Cities you want to hear about</h2>
      <div className="row">
        <input
          value={cityInput}
          onChange={(e) => setCityInput(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && handleAddCity()}
          placeholder="e.g. Paris"
          list="city-suggestions"
        />
        <datalist id="city-suggestions">
          {CITY_SUGGESTIONS.map((city) => (
            <option key={city} value={city} />
          ))}
        </datalist>
        <button onClick={handleAddCity}>Add city</button>
      </div>

      <h3>Your cities</h3>
      {myCities.length === 0 && <p className="muted">No cities added yet - add one above to start receiving notifications.</p>}
      <ul>
        {myCities.map((city) => (
          <li key={city}>
            {city} <button onClick={() => handleRemoveCity(city)}>Remove</button>
          </li>
        ))}
      </ul>

      <h3>Available slots (pushed live, no polling)</h3>
      {message && <p className="message">{message}</p>}
      {visibleSlots.length === 0 && <p className="muted">Waiting for a slot notification...</p>}
      <ul>
        {visibleSlots.map((slot) => (
          <li key={slot.id}>
            [{slot.cityName}] {new Date(slot.startTime).toLocaleString()} -{" "}
            {new Date(slot.endTime).toLocaleString()}{" "}
            <button onClick={() => handleReserve(slot)}>Reserve</button>
          </li>
        ))}
      </ul>
    </div>
  );
}
