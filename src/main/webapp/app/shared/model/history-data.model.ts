import { IHistory } from 'app/shared/model/history.model';

export interface IHistoryData {
  id?: string;
  key?: string | null;
  valueString?: string | null;
  valueInt?: number | null;
  history?: IHistory | null;
}

export const defaultValue: Readonly<IHistoryData> = {};
