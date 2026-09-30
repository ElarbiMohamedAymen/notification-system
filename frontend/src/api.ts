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

export function subscribeCity(userId: string, cityName: string) {
  return request<void>(`/api/cities/${encodeURIComponent(cityName)}/subscribe`, {
    method: "POST",
    body: JSON.stringify({ userId }),
  });
}

export function unsubscribeCity(userId: string, cityName: string) {
  return request<void>(
    `/api/cities/${encodeURIComponent(cityName)}/subscribe?userId=${encodeURIComponent(userId)}`,
    { method: "DELETE" },
  );
}

export function mySubscriptions(userId: string) {
  return request<string[]>(`/api/cities/mine?userId=${encodeURIComponent(userId)}`);
}

export function availableSlots(cityName: string) {
  return request<SlotDto[]>(`/api/cities/${encodeURIComponent(cityName)}/slots`);
}

export function publishSlot(cityName: string, startTime: string, endTime: string) {
  return request<SlotDto>(`/api/admin/slots`, {
    method: "POST",
    body: JSON.stringify({ cityName, startTime, endTime }),
  });
}

export function reserveSlot(slotId: number, userId: string) {
  return request<SlotDto>(`/api/slots/${slotId}/reserve`, {
    method: "POST",
    body: JSON.stringify({ userId }),
  });
}
