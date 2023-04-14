import dayjs from 'dayjs';

export interface IStatistics {
  id?: number;
  added?: string | null;
  count?: number | null;
}

export const defaultValue: Readonly<IStatistics> = {};
