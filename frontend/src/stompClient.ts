import { Client } from "@stomp/stompjs";
import { API_BASE } from "./api";

let client: Client | null = null;

export function getStompClient(): Client {
  if (client) {
    return client;
  }
  client = new Client({
    brokerURL: `${API_BASE.replace(/^http/, "ws")}/ws`,
    reconnectDelay: 3000,
  });
  client.activate();
  return client;
}
