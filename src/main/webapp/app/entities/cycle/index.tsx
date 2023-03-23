import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Cycle from './cycle';
import CycleDetail from './cycle-detail';
import CycleUpdate from './cycle-update';
import CycleDeleteDialog from './cycle-delete-dialog';

const CycleRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Cycle />} />
    <Route path="new" element={<CycleUpdate />} />
    <Route path=":id">
      <Route index element={<CycleDetail />} />
      <Route path="edit" element={<CycleUpdate />} />
      <Route path="delete" element={<CycleDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CycleRoutes;
