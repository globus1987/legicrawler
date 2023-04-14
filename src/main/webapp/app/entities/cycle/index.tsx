import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import CycleDetail from './cycle-detail';

const CycleRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route path=":id">
      <Route index element={<CycleDetail />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CycleRoutes;
