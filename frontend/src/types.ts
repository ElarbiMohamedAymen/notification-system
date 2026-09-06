export interface SlotDto {
  id: number;
  topicName: string;
  startTime: string;
  endTime: string;
  status: "AVAILABLE" | "RESERVED";
  reservedBy: string | null;
}
