import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Collection from './collection';
import CollectionDetail from './collection-detail';
import CollectionUpdate from './collection-update';
import CollectionDeleteDialog from './collection-delete-dialog';

const CollectionRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Collection />} />
    <Route path="new" element={<CollectionUpdate />} />
    <Route path=":id">
      <Route index element={<CollectionDetail />} />
      <Route path="edit" element={<CollectionUpdate />} />
      <Route path="delete" element={<CollectionDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CollectionRoutes;
