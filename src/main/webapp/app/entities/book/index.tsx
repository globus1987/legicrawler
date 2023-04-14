import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Book from './book';
import BookDetail from './book-detail';
import BookStats from './bookstats';

const BookRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Book />} />
    <Route path="stats" element={<BookStats />} />
    <Route path=":id">
      <Route index element={<BookDetail />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default BookRoutes;
