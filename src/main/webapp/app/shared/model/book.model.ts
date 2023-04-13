import dayjs from 'dayjs';
import { ICycle } from 'app/shared/model/cycle.model';
import { ICollection } from 'app/shared/model/collection.model';
import { IAuthor } from 'app/shared/model/author.model';

export interface IBook {
  id?: string;
  title?: string | null;
  url?: string | null;
  imgsrc?: string | null;
  ebook?: boolean | null;
  audiobook?: boolean | null;
  category?: string | null;
  added?: string | null;
  kindleSubscription?: boolean | null;
  libraryPass?: boolean | null;
  librarySubscription?: boolean | null;
  subscription?: boolean | null;
  cycle?: ICycle | null;
  collections?: ICollection[] | null;
  authors?: IAuthor[] | null;
}

export const defaultValue: Readonly<IBook> = {
  ebook: false,
  audiobook: false,
  kindleSubscription: false,
  libraryPass: false,
  librarySubscription: false,
  subscription: false,
};
