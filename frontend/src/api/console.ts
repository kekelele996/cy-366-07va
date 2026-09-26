import { API_BASE_URL } from "../constants/app";
import type { Seat, UsageSession } from "../types";

async function getJson<T>(path: string): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: { Accept: "application/json" },
  });
  if (!response.ok) {
    throw new Error(`运营台请求失败：${response.status}`);
  }
  return response.json() as Promise<T>;
}

export function fetchSeats(): Promise<Seat[]> {
  return getJson("/console/seats");
}

export function fetchActiveSessions(): Promise<UsageSession[]> {
  return getJson("/console/sessions/active");
}

export function fetchRecentSessions(): Promise<UsageSession[]> {
  return getJson("/console/sessions/recent");
}
