import React, { useEffect } from 'react';
import { GoBack } from 'app/entities/ReusableComponents';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities } from 'app/entities/statistics/statistics.reducer';
import { Line } from 'react-chartjs-2';
import Chart from 'app/entities/book/chart';

export const BookStats = () => {
  const dispatch = useAppDispatch();
  const statisticsList = useAppSelector(state => state.statistics.entities);
  const loading = useAppSelector(state => state.statistics.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  return (
    <div>
      <Chart chartData={statisticsList} chartType="day" />
      <Chart chartData={statisticsList} chartType="week" />
      <Chart chartData={statisticsList} chartType="month" />
      <GoBack to={'/book'} />
    </div>
  );
};

export default BookStats;
