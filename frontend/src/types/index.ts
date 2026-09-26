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

export interface ReservationArrival {
  reservationId: number;
  reservationNo: string;
  memberId: number;
  memberNo: string;
  memberName: string;
  memberPhone: string;
  balance: number;
  packageMinutes: number | null;
  stationId: number;
  stationNo: string;
  zoneName: string;
  stationStatus: string;
  hourlyRate: number;
  startTime: string;
  endTime: string;
  status: string;
}

export interface ChargeLine {
  id: number;
  sessionId: number;
  memberId: number;
  memberName: string;
  stationNo: string;
  chargeType: "package" | "balance" | string;
  minutes: number;
  amount: number;
  detail: string;
  createdAt: string;
}

export interface CheckInResult {
  success: boolean;
  code: string;
  message: string;
  sessionId: number | null;
  sessionNo: string | null;
  memberName: string;
  stationNo: string;
  startedAt: string | null;
  firstHourCharge: number;
  packageMinutesUsed: number;
  balanceAmountUsed: number;
  shortage: number;
  charges: ChargeLine[];
}

export interface ActiveStation {
  sessionId: number;
  sessionNo: string;
  stationId: number;
  stationNo: string;
  zoneName: string;
  memberId: number;
  memberNo: string;
  memberName: string;
  reservationNo: string | null;
  startedAt: string;
  firstHourCharge: number;
  packageMinutesUsed: number;
  balanceAmountUsed: number;
}

export interface ChargeListResponse {
  charges: ChargeLine[];
}
