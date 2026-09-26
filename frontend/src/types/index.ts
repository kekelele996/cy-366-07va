export interface FeatureItem {
  id: number;
  title: string;
  description: string;
  status: string;
  metric: string;
}

export interface KpiItem {
  label: string;
  value: string;
  trend: string;
  tone: string;
}

export interface OperationRecord {
  key: string;
  name: string;
  owner: string;
  status: string;
  metric: string;
  priority: string;
}

export interface OverviewResponse {
  appName: string;
  appCode: string;
  description: string;
  features: FeatureItem[];
  kpis: KpiItem[];
  records: OperationRecord[];
}

export type SeatStatus = "IDLE" | "IN_USE" | "RESERVED" | "FAULT";

export interface Seat {
  id: number;
  seatNo: string;
  area: string;
  zone: string;
  seatType: string;
  status: SeatStatus;
  hourlyRate: number;
  createdAt?: string;
}

export interface Reservation {
  id: number;
  reservationNo: string;
  memberId: number;
  seatId: number;
  startTime: string;
  endTime: string;
  status: "RESERVED" | "CHECKED_IN" | "CANCELLED";
  memberNo: string;
  memberName: string;
  memberBalance: number;
  seatNo: string;
  seatArea: string;
  seatType: string;
  seatStatus: SeatStatus;
  hourlyRate: number;
  packageRemainingMinutes: number;
}

export interface ChargeDetail {
  chargeType: "PACKAGE" | "BALANCE";
  minutes: number;
  amount: number;
}

export interface CheckinResult {
  success: boolean;
  message: string;
  reservationId: number;
  reservationNo: string;
  sessionId: number | null;
  memberId: number;
  memberName: string;
  seatId: number;
  seatNo: string;
  startTime: string | null;
  planEndTime: string | null;
  firstHourMinutes: number | null;
  firstHourFee: number;
  packageMinutesUsed: number;
  balanceUsed: number;
  shortfall: number;
  remainingBalance: number;
  remainingPackageMinutes: number;
  seatStatus: SeatStatus | null;
  charges: ChargeDetail[];
}

export interface Charge {
  id: number;
  chargeType: "PACKAGE" | "BALANCE";
  minutes: number;
  amount: number;
  createdAt: string;
}

export interface UsageSession {
  id: number;
  memberId: number;
  seatId: number;
  reservationId: number | null;
  startTime: string;
  planEndTime: string;
  endTime: string | null;
  status: "IN_USE" | "FINISHED";
  chargedMinutes: number;
  chargeSummary: string;
  memberNo: string;
  memberName: string;
  seatNo: string;
  seatArea: string;
  seatType: string;
  hourlyRate: number;
  reservationNo: string | null;
  charges: Charge[];
}
