import dayjs from 'dayjs';
import { IHistoryData } from 'app/shared/model/history-data.model';

export interface IHistory {
  id?: string;
  timeStamp?: string | null;
  data?: IHistoryData[] | null;
}

export const defaultValue: Readonly<IHistory> = {};
