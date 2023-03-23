import { IBook } from 'app/shared/model/book.model';

export interface ICycle {
  id?: string;
  name?: string | null;
  url?: string | null;
  books?: IBook[] | null;
}

export const defaultValue: Readonly<ICycle> = {};
