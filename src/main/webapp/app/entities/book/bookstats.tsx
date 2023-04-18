import React, { useEffect } from 'react';
import { DetailHeader, GoBack } from 'app/entities/ReusableComponents';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities } from 'app/entities/statistics/statistics.reducer';
import { Line } from 'react-chartjs-2';
import Chart from 'app/entities/book/chart';
import { useNavigate } from 'react-router-dom';
import { Col, Row } from 'reactstrap';

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
    navigate('/book?page=1&sort=id,asc');
  };

  return (
    <div>
      <Row>
        <Col>
          {' '}
          <Chart chartData={statisticsList} chartType="day" onBarClick={handleBarClick} color="#FFA552" />
        </Col>
        <Col>
          {' '}
          <Chart chartData={statisticsList} chartType="week" onBarClick={handleBarClick} color="#BA5624" />
        </Col>
        <Col>
          {' '}
          <Chart chartData={statisticsList} chartType="month" onBarClick={handleBarClick} color="#381D2A" />
        </Col>
      </Row>
      <GoBack to={'/book'} />
    </div>
  );
};

export default BookStats;
