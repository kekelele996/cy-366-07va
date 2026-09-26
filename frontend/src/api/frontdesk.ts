import { API_BASE_URL } from "../constants/app";
import type { CheckinResult, Reservation } from "../types";

async function parseJsonOrThrow<T>(response: Response): Promise<T> {
  const text = await response.text();
  let payload: unknown = null;
  if (text) {
    try {
      payload = JSON.parse(text);
    } catch {
      payload = null;
    }
  }
  if (!response.ok) {
    const message =
      payload && typeof payload === "object" && "message" in payload
        ? String((payload as { message: unknown }).message)
        : `请求失败（${response.status}）`;
    throw new Error(message);
  }
  return payload as T;
}

/** 前台预约单列表，默认只看待开机的预约。 */
export async function fetchReservations(status: "RESERVED" | "CHECKED_IN" | "ALL" = "RESERVED"): Promise<Reservation[]> {
  const query = status === "ALL" ? "" : `?status=${status}`;
  const response = await fetch(`${API_BASE_URL}/front/reservations${query}`, {
    headers: { Accept: "application/json" },
  });
  return parseJsonOrThrow<Reservation[]>(response);
}

export async function fetchReservation(id: number): Promise<Reservation> {
  const response = await fetch(`${API_BASE_URL}/front/reservations/${id}`, {
    headers: { Accept: "application/json" },
  });
  return parseJsonOrThrow<Reservation>(response);
}

/** 到店开机：余额不足时 HTTP 200 且 success=false（预约与机位保持原状，带差额提示）。 */
export async function checkinReservation(id: number): Promise<CheckinResult> {
  const response = await fetch(`${API_BASE_URL}/front/reservations/${id}/checkin`, {
    method: "POST",
    headers: { Accept: "application/json" },
  });
  return parseJsonOrThrow<CheckinResult>(response);
}
