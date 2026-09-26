export function formatMoney(value: number | null | undefined): string {
  return `¥${(value ?? 0).toFixed(2)}`;
}

export function formatDateTime(value: string | null | undefined): string {
  if (!value) {
    return "—";
  }
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }
  const pad = (n: number) => String(n).padStart(2, "0");
  return `${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`;
}

export function formatTimeRange(start: string, end: string): string {
  return `${formatDateTime(start)} ~ ${formatDateTime(end)}`;
}

export const STATION_STATUS_LABELS: Record<string, string> = {
  idle: "空闲",
  in_use: "使用中",
  reserved: "预约",
  fault: "故障",
};

export const CHARGE_TYPE_LABELS: Record<string, string> = {
  package: "时长包",
  balance: "余额",
};
