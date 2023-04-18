import React, { useEffect, useState } from 'react';
import { DetailHeader, GoBack } from 'app/entities/ReusableComponents';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities } from 'app/entities/statistics/statistics.reducer';
import { Line } from 'react-chartjs-2';
import Chart from 'app/entities/book/chart';
import { useNavigate } from 'react-router-dom';
import { Dropdown, DropdownToggle, DropdownMenu, DropdownItem, Row, Col } from 'reactstrap';
import { format, startOfWeek, startOfMonth, subMonths } from 'date-fns';

export const BookStats = () => {
  const dispatch = useAppDispatch();
  const statisticsList = useAppSelector(state => state.statistics.entities);
  const loading = useAppSelector(state => state.statistics.loading);
  const navigate = useNavigate();
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const [selectedFilter, setSelectedFilter] = useState('day');

  const toggleDropdown = () => setDropdownOpen(prevState => !prevState);

  const handleFilterChange = filter => {
    setSelectedFilter(filter);
  };
  let chartData = statisticsList;
  useEffect(() => {
    chartData = getFilteredData();
  }, [selectedFilter]);

  const getFilteredData = () => {
    let filteredData = statisticsList;

    if (selectedFilter === 'lastWeek') {
      const weekStart = format(subWeeks(new Date(), 1), 'yyyy-MM-dd');
      return statisticsList.filter(data => data.added >= weekStart);
    } else if (selectedFilter === 'lastTwoWeeks') {
      const weekStart = format(subWeeks(new Date(), 2), 'yyyy-MM-dd');
      return statisticsList.filter(data => data.added >= weekStart);
    } else if (selectedFilter === 'lastMonth') {
      const monthStart = format(subMonths(new Date(), 1), 'yyyy-MM-dd');
      return statisticsList.filter(data => data.added >= monthStart);
    }

    return filteredData;
  };

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
          <Dropdown isOpen={dropdownOpen} toggle={toggleDropdown}>
            <DropdownToggle caret>{selectedFilter}</DropdownToggle>
            <DropdownMenu>
              <DropdownItem onClick={() => handleFilterChange('last-week')}>Last Week</DropdownItem>
              <DropdownItem onClick={() => handleFilterChange('last-two-weeks')}>Last Two Weeks</DropdownItem>
              <DropdownItem onClick={() => handleFilterChange('last-month')}>Last Month</DropdownItem>
            </DropdownMenu>
          </Dropdown>
        </Col>
        <Col>
          <Chart chartData={chartData} chartType="day" onBarClick={handleBarClick} color="#FFA552" />
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
function subWeeks(arg0: Date, arg1: number): any {
  throw new Error('Function not implemented.');
}
