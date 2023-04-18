import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import History from './history';

const HistoryRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<History />} />
  </ErrorBoundaryRoutes>
);

export default HistoryRoutes;
