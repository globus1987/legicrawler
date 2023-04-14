import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Statistics from './statistics';

const StatisticsRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Statistics />} />
    <Route path=":id"></Route>
  </ErrorBoundaryRoutes>
);

export default StatisticsRoutes;
