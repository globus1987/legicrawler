import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Book from './book';
import BookDetail from './book-detail';

const BookRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Book />} />
    <Route path=":id">
      <Route index element={<BookDetail />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default BookRoutes;
