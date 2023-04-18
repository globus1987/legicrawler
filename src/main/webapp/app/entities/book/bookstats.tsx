import React, { useEffect, useState } from 'react';
import { GoBack } from 'app/entities/ReusableComponents';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities } from 'app/entities/statistics/statistics.reducer';
import Chart from 'app/entities/book/chart';
import { useNavigate } from 'react-router-dom';
import { Col, Row } from 'reactstrap';
import { format, subDays } from 'date-fns';
import { ValidatedField } from 'react-jhipster';

export const BookStats = () => {
  const dispatch = useAppDispatch();
  const statisticsList = useAppSelector(state => state.statistics.entities);
  const statisticsLoading = useAppSelector(state => state.statistics.loading);
  const loading = useAppSelector(state => state.statistics.loading);
  const navigate = useNavigate();
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const [selectedFilter, setSelectedFilter] = useState('');
  const [chartData, setChartData] = useState();

  const toggleDropdown = () => setDropdownOpen(prevState => !prevState);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  useEffect(() => {
    if (statisticsList.length > 0) setChartData(statisticsList);
  }, [statisticsLoading]);

  const handleFilterChange = filter => {
    setSelectedFilter(filter);
  };
  useEffect(() => {
    setChartData(getFilteredData());
  }, [selectedFilter]);

  const getFilteredData = () => {
    let filteredData = statisticsList;

    if (selectedFilter.length > 0) {
      const start = format(subDays(new Date(), Number(selectedFilter) - 1), 'yyyy-MM-dd');
      return statisticsList.filter(data => data.added >= start);
    }

    return filteredData;
  };

  const handleBarClick = (label: string, value: number) => {
    window.localStorage.clear();
    window.localStorage.setItem('filter.added', new Date(label).toISOString());
    navigate('/book?page=1&sort=id,asc');
  };

  return (
    <div>
      <Row>
        <Col>
          <ValidatedField
            type="text"
            name="filter"
            label="Nr of days"
            value={selectedFilter}
            onChange={e => handleFilterChange(e.target.value)}
          />
          {chartData && <Chart chartData={chartData} chartType="day" onBarClick={handleBarClick} color="#FFA552" />}
        </Col>
        <Col>
          <Chart chartData={statisticsList} chartType="week" onBarClick={handleBarClick} color="#BA5624" />
        </Col>
        <Col>
          <Chart chartData={statisticsList} chartType="month" onBarClick={handleBarClick} color="#381D2A" />
        </Col>
      </Row>
      <GoBack to={'/book'} />
    </div>
  );
};

export default BookStats;
