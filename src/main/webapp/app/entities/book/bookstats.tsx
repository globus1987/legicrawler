import React, { useEffect } from 'react';
import { GoBack } from 'app/entities/ReusableComponents';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities } from 'app/entities/statistics/statistics.reducer';
import { Line } from 'react-chartjs-2';
import Chart from 'app/entities/book/chart';
import { useNavigate } from 'react-router-dom';

export const BookStats = () => {
  const dispatch = useAppDispatch();
  const statisticsList = useAppSelector(state => state.statistics.entities);
  const loading = useAppSelector(state => state.statistics.loading);
  const navigate = useNavigate();

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);
  const handleBarClick = (label: string, value: number) => {
    window.localStorage.clear();
    window.localStorage.setItem('filter.added', new Date(label).toISOString());
    navigate('/book');
  };

  return (
    <div>
      <Chart chartData={statisticsList} chartType="day" onBarClick={handleBarClick} />
      <Chart chartData={statisticsList} chartType="week" onBarClick={handleBarClick} />
      <Chart chartData={statisticsList} chartType="month" onBarClick={handleBarClick} />
      <GoBack to={'/book'} />
    </div>
  );
};

export default BookStats;
