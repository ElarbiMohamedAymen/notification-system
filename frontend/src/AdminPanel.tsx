import { useState } from "react";
import * as api from "./api";

function toLocalInputValue(date: Date): string {
  const pad = (n: number) => String(n).padStart(2, "0");
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
}

export default function AdminPanel() {
  const [topicName, setTopicName] = useState("station-42");
  const [startTime, setStartTime] = useState(toLocalInputValue(new Date(Date.now() + 60 * 60 * 1000)));
  const [endTime, setEndTime] = useState(toLocalInputValue(new Date(Date.now() + 2 * 60 * 60 * 1000)));
  const [message, setMessage] = useState<string | null>(null);

  async function handlePublish() {
    setMessage(null);
    try {
      const slot = await api.publishSlot(topicName, new Date(startTime).toISOString(), new Date(endTime).toISOString());
      setMessage(`Published slot #${slot.id} on "${topicName}" — pushed to subscribers.`);
    } catch (e) {
      setMessage(e instanceof Error ? e.message : "Failed to publish slot");
    }
  }

  return (
    <div>
      <h2>Publish an available slot</h2>
      <div className="row">
        <label>
          Topic
          <input value={topicName} onChange={(e) => setTopicName(e.target.value)} />
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
