import type { SlotDto } from "./types";

export const API_BASE = "http://localhost:8080";

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const res = await fetch(`${API_BASE}${path}`, {
    headers: { "Content-Type": "application/json" },
    ...options,
  });
  if (!res.ok) {
    const body = await res.json().catch(() => ({ error: res.statusText }));
    throw new Error(body.error ?? `Request failed: ${res.status}`);
  }
  const text = await res.text();
  return (text ? JSON.parse(text) : undefined) as T;
}

export function subscribeTopic(userId: string, topicName: string) {
  return request<void>(`/api/topics/${encodeURIComponent(topicName)}/subscribe`, {
    method: "POST",
    body: JSON.stringify({ userId }),
  });
}

export function unsubscribeTopic(userId: string, topicName: string) {
  return request<void>(
    `/api/topics/${encodeURIComponent(topicName)}/subscribe?userId=${encodeURIComponent(userId)}`,
    { method: "DELETE" },
  );
}

export function mySubscriptions(userId: string) {
  return request<string[]>(`/api/topics/mine?userId=${encodeURIComponent(userId)}`);
}

export function availableSlots(topicName: string) {
  return request<SlotDto[]>(`/api/topics/${encodeURIComponent(topicName)}/slots`);
}

export function publishSlot(topicName: string, startTime: string, endTime: string) {
  return request<SlotDto>(`/api/admin/slots`, {
    method: "POST",
    body: JSON.stringify({ topicName, startTime, endTime }),
  });
}

export function reserveSlot(slotId: number, userId: string) {
  return request<SlotDto>(`/api/slots/${slotId}/reserve`, {
    method: "POST",
    body: JSON.stringify({ userId }),
  });
}
