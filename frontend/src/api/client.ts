import { API_BASE_URL } from "../constants/app";
import type {
  ActiveStation,
  ChargeListResponse,
  CheckInResult,
  OverviewResponse,
  ReservationArrival,
} from "../types";

async function getJson<T>(path: string): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: { Accept: "application/json" },
  });
  if (!response.ok) {
    const body = (await response.json().catch(() => null)) as { message?: string } | null;
    throw new Error(body?.message ?? `请求失败：${response.status}`);
  }
  return response.json() as Promise<T>;
}

export async function fetchOverview(): Promise<OverviewResponse> {
  return getJson<OverviewResponse>("/overview");
}

export async function fetchArrivals(): Promise<ReservationArrival[]> {
  return getJson<ReservationArrival[]>("/front-desk/reservations");
}

export async function checkIn(reservationId: number): Promise<CheckInResult> {
  const response = await fetch(`${API_BASE_URL}/front-desk/check-in`, {
    method: "POST",
    headers: { "Content-Type": "application/json", Accept: "application/json" },
    body: JSON.stringify({ reservationId }),
  });
  if (!response.ok) {
    const body = (await response.json().catch(() => null)) as { message?: string } | null;
    throw new Error(body?.message ?? `开机失败：${response.status}`);
  }
  return response.json() as Promise<CheckInResult>;
}

export async function fetchActiveStations(): Promise<ActiveStation[]> {
  return getJson<ActiveStation[]>("/front-desk/active-stations");
}

export async function fetchCharges(limit = 50): Promise<ChargeListResponse> {
  return getJson<ChargeListResponse>(`/front-desk/charges?limit=${limit}`);
}
