import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import AuthorDetail from './author-detail';

const AuthorRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route path=":id">
      <Route index element={<AuthorDetail />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default AuthorRoutes;
