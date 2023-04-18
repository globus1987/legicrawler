import React, { useEffect, useState } from 'react';
import { GoBack } from 'app/entities/ReusableComponents';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getStatistics } from 'app/entities/statistics/statistics.reducer';
import { getEntities as getHistory } from 'app/entities/history/history.reducer';
import Chart from 'app/entities/book/chart';
import { useNavigate } from 'react-router-dom';
import { Col, Row, Table } from 'reactstrap';
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
  const historyList = useAppSelector(state => state.history.entities);
  const historyLoading = useAppSelector(state => state.history.loading);

  useEffect(() => {
    dispatch(getStatistics({}));
    dispatch(getHistory({}));
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
          {chartData && <Chart chartData={chartData} chartType="day" onBarClick={handleBarClick} color="#FFA552" />}
          <ValidatedField
            type="text"
            name="filter"
            label="Nr of days"
            value={selectedFilter}
            onChange={e => handleFilterChange(e.target.value)}
          />
        </Col>
        <Col>
          <Chart chartData={statisticsList} chartType="week" onBarClick={handleBarClick} color="#BA5624" />
        </Col>
        <Col>
          <Chart chartData={statisticsList} chartType="month" onBarClick={handleBarClick} color="#381D2A" />
        </Col>
      </Row>
      <Row>
        <Table responsive>
          <thead>
            <tr>
              <th>Time Stamp</th>
              <th>Database</th>
              <th>Parsed</th>
              <th>New</th>
              <th>Deleted</th>
              <th>Error</th>
            </tr>
          </thead>
          <tbody>
            {historyList.map((history, index) => (
              <tr key={history.id}>
                <td>{history.timeStamp}</td>
                <td
                  style={{
                    color:
                      index + 1 < historyList.length &&
                      history.data?.find(data => data.key === 'previous')?.valueInt !==
                        historyList[index + 1].data?.find(data => data.key === 'previous')?.valueInt
                        ? 'red'
                        : undefined,
                    fontWeight:
                      index + 1 < historyList.length &&
                      history.data?.find(data => data.key === 'previous')?.valueInt !==
                        historyList[index + 1].data?.find(data => data.key === 'previous')?.valueInt
                        ? 'bold'
                        : undefined,
                  }}
                >
                  {history.data?.find(data => data.key === 'previous')?.valueInt}
                </td>
                <td
                  style={{
                    color:
                      index + 1 < historyList.length &&
                      history.data?.find(data => data.key === 'parsed')?.valueInt !==
                        historyList[index + 1].data?.find(data => data.key === 'parsed')?.valueInt
                        ? 'red'
                        : undefined,
                    fontWeight:
                      index + 1 < historyList.length &&
                      history.data?.find(data => data.key === 'parsed')?.valueInt !==
                        historyList[index + 1].data?.find(data => data.key === 'parsed')?.valueInt
                        ? 'bold'
                        : undefined,
                  }}
                >
                  {history.data?.find(data => data.key === 'parsed')?.valueInt}
                </td>
                <td
                  style={{
                    color:
                      index + 1 < historyList.length && history.data?.find(data => data.key === 'new')?.valueInt !== 0 ? 'red' : undefined,
                    fontWeight:
                      index + 1 < historyList.length && history.data?.find(data => data.key === 'new')?.valueInt !== 0 ? 'bold' : undefined,
                  }}
                >
                  {history.data?.find(data => data.key === 'new')?.valueInt}
                </td>

                <td
                  style={{
                    color:
                      index + 1 < historyList.length && history.data?.find(data => data.key === 'deleted')?.valueInt !== 0
                        ? 'red'
                        : undefined,
                    fontWeight:
                      index + 1 < historyList.length && history.data?.find(data => data.key === 'deleted')?.valueInt !== 0
                        ? 'bold'
                        : undefined,
                  }}
                >
                  {history.data?.find(data => data.key === 'deleted')?.valueInt}
                </td>
                <td
                  style={{
                    color:
                      index + 1 < historyList.length &&
                      history.data?.find(data => data.key === 'error')?.valueInt !==
                        historyList[index + 1].data?.find(data => data.key === 'error')?.valueInt
                        ? 'red'
                        : undefined,
                    fontWeight:
                      index + 1 < historyList.length &&
                      history.data?.find(data => data.key === 'error')?.valueInt !==
                        historyList[index + 1].data?.find(data => data.key === 'error')?.valueInt
                        ? 'bold'
                        : undefined,
                  }}
                >
                  {history.data?.find(data => data.key === 'error')?.valueInt}
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
      </Row>
      <GoBack to={'/book'} />
    </div>
  );
};

export default BookStats;
