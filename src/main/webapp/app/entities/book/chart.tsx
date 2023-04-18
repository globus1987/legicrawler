import React, { useState } from 'react';
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
  color: string;
  onBarClick: (label: string, value: number) => void;
}

const BarChart: React.FC<IChartProps> = ({ chartData, chartType, onBarClick, color }) => {
  const [timePeriod, setTimePeriod] = useState<'day' | 'lastWeek' | 'lastTwoWeeks' | 'lastMonth'>('day');

  let data = chartData;

  if (timePeriod !== 'day') {
    let startDate;

    switch (timePeriod) {
      case 'lastWeek':
        startDate = subWeeks(new Date(), 1);
        break;
      case 'lastTwoWeeks':
        startDate = subWeeks(new Date(), 2);
        break;
      case 'lastMonth':
        startDate = subMonths(new Date(), 1);
        break;
      default:
        break;
    }

    data = data.filter(d => new Date(d.added) >= startDate);
  }
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
    labels: data.map(data => data.added).sort(),
    datasets: [
      {
        label: 'Books per ' + chartType,
        data: data.map(data => data.count),
        backgroundColor: color,
        hoverBorderColor: 'black',
        borderWidth: 1,
        borderRadius: 5,
        pointStyle: 'star',
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
        padding: {
          top: 10,
          bottom: 30,
        },
        font: {
          weight: 'bold',
          size: 20,
        },
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
      if (elements.length > 0 && chartType === 'day') {
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
function subWeeks(arg0: Date, arg1: number): any {
  throw new Error('Function not implemented.');
}

function subMonths(arg0: Date, arg1: number): any {
  throw new Error('Function not implemented.');
}
