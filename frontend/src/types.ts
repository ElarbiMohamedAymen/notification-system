export interface SlotDto {
  id: number;
  cityName: string;
  startTime: string;
  endTime: string;
  status: "AVAILABLE" | "RESERVED";
  reservedBy: string | null;
}
