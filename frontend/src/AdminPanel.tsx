import { useState } from "react";
import * as api from "./api";

const CITY_SUGGESTIONS = ["Paris", "Lyon", "Marseille", "Tunis", "Sfax", "Sousse"];

function toLocalInputValue(date: Date): string {
  const pad = (n: number) => String(n).padStart(2, "0");
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
}

export default function AdminPanel() {
  const [cityName, setCityName] = useState("Paris");
  const [startTime, setStartTime] = useState(toLocalInputValue(new Date(Date.now() + 60 * 60 * 1000)));
  const [endTime, setEndTime] = useState(toLocalInputValue(new Date(Date.now() + 2 * 60 * 60 * 1000)));
  const [message, setMessage] = useState<string | null>(null);

  async function handlePublish() {
    setMessage(null);
    try {
      const slot = await api.publishSlot(cityName, new Date(startTime).toISOString(), new Date(endTime).toISOString());
      setMessage(`Published slot #${slot.id} for "${cityName}" — pushed to everyone listening for that city.`);
    } catch (e) {
      setMessage(e instanceof Error ? e.message : "Failed to publish slot");
    }
  }

  return (
    <div>
      <h2>Publish an available slot</h2>
      <div className="row">
        <label>
          City
          <input value={cityName} onChange={(e) => setCityName(e.target.value)} list="city-suggestions" />
          <datalist id="city-suggestions">
            {CITY_SUGGESTIONS.map((city) => (
              <option key={city} value={city} />
            ))}
          </datalist>
        </label>
      </div>
      <div className="row">
        <label>
          Start
          <input type="datetime-local" value={startTime} onChange={(e) => setStartTime(e.target.value)} />
        </label>
        <label>
          End
          <input type="datetime-local" value={endTime} onChange={(e) => setEndTime(e.target.value)} />
        </label>
      </div>
      <button onClick={handlePublish}>Publish slot</button>
      {message && <p className="message">{message}</p>}
    </div>
  );
}
