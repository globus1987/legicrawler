import React from 'react';
import { Bar } from 'react-chartjs-2';
import { format, startOfWeek, startOfMonth } from 'date-fns';

import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend, Chart } from 'chart.js';

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

interface IChartData {
  added: string;
  count: number;
}

interface IChartProps {
  chartData: IChartData[];
  chartType: string;
  onBarClick: (label: string, value: number) => void;
}

const BarChart: React.FC<IChartProps> = ({ chartData, chartType, onBarClick }) => {
  let data = chartData;

  if (chartType === 'week') {
    data = chartData.reduce((acc, curr) => {
      const date = new Date(curr.added);
      const weekStart = startOfWeek(date, { weekStartsOn: 1 }); // assuming Monday is the first day of the week
      const weekLabel = format(weekStart, 'yyyy-MM-dd');
      const index = acc.findIndex(data => data.added === weekLabel);

      if (index === -1) {
        acc.push({ added: weekLabel, count: curr.count });
      } else {
        acc[index].count += curr.count;
      }

      return acc;
    }, []);
  } else if (chartType === 'month') {
    // group by month
    data = chartData.reduce((acc, curr) => {
      const date = new Date(curr.added);
      const monthStart = startOfMonth(date);
      const monthLabel = format(monthStart, 'MMMM yyyy'); // format the label as "April 2023"
      const index = acc.findIndex(data => data.added === monthLabel);

      if (index === -1) {
        acc.push({ added: monthLabel, count: curr.count });
      } else {
        acc[index].count += curr.count;
      }

      return acc;
    }, []);
  }

  // same logic for month

  const loaded = data.map(data => data.added).length > 0 && data.map(data => data.count).length > 0;

  const dataChart = {
    labels: chartData.map(data => data.added),
    datasets: [
      {
        label: 'Books per ' + chartType,
        data: chartData.map(data => data.count),
        backgroundColor: 'rgba(75,192,192,0.4)',
        borderColor: 'rgba(75,192,192,1)',
        borderWidth: 1,
      },
    ],
  };

  const options = {
    responsive: true,
    plugins: {
      legend: {
        display: false,
      },
      title: {
        display: true,
        text: 'Books by ' + chartType,
      },
    },
    scale: {
      x: {
        type: 'time',
        time: {
          unit: chartType.toLowerCase(),
        },
      },
    },
    onClick: (_, elements) => {
      if (elements.length > 0) {
        const index = elements[0].index;
        const label = dataChart.labels[index];
        const value = dataChart.datasets[0].data[index];
        onBarClick(label, value);
      }
    },
  };

  return <>{loaded && <Bar data={dataChart} options={options} />}</>;
};

export default BarChart;
