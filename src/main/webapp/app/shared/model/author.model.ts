import { IBook } from 'app/shared/model/book.model';

export interface IAuthor {
  id?: string;
  name?: string | null;
  books?: IBook[] | null;
}

export const defaultValue: Readonly<IAuthor> = {};
