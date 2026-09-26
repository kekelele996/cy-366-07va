export function formatDateTime(value: string | null | undefined): string {
  if (!value) {
    return "—";
  }
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }
  const pad = (n: number) => String(n).padStart(2, "0");
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(
    date.getHours(),
  )}:${pad(date.getMinutes())}`;
}

export function formatTime(value: string | null | undefined): string {
  if (!value) {
    return "—";
  }
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }
  const pad = (n: number) => String(n).padStart(2, "0");
  return `${pad(date.getHours())}:${pad(date.getMinutes())}`;
}

export function formatMoney(value: number | null | undefined): string {
  const amount = Number(value ?? 0);
  return amount.toFixed(2);
}

export function seatStatusLabel(status: string): string {
  switch (status) {
    case "IDLE":
      return "空闲";
    case "IN_USE":
      return "使用中";
    case "RESERVED":
      return "已预约";
    case "FAULT":
      return "故障";
    default:
      return status;
  }
}

export function chargeTypeLabel(type: string): string {
  return type === "PACKAGE" ? "时长包" : "余额";
}
