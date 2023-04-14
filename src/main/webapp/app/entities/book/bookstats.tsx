import React, { useEffect } from 'react';
import { GoBack } from 'app/entities/ReusableComponents';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getBookStats } from 'app/entities/book/bookstat.reducer';

export const BookStats = () => {
  const dispatch = useAppDispatch();
  const bookList = useAppSelector(state => state.bookstat.entities);
  const loading = useAppSelector(state => state.bookstat.loading);
  useEffect(() => {
    dispatch(getBookStats);
  }, []);

  if (!loading) console.log(JSON.stringify(bookList));
  return (
    <div>
      <GoBack to={'/book'} />
    </div>
  );
};

export default BookStats;
