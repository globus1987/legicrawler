import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import CollectionDetail from './collection-detail';

const CollectionRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route path=":id">
      <Route index element={<CollectionDetail />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CollectionRoutes;
