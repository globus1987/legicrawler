import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

import { Table } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

const BookTable = ({ bookList, sort }) => {
  const [width, setWidth] = useState(window.innerWidth);
  useEffect(() => {
    const handleResize = () => setWidth(window.innerWidth);
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);
  const renderMinimumHeaders = () => (
    <>
      <th className="hand" onClick={sort('title')}>
        Title <FontAwesomeIcon icon="sort" />
      </th>
      <th className="hand">Authors</th>
      <th className="hand" onClick={sort('category')}>
        Category <FontAwesomeIcon icon="sort" />
      </th>
    </>
  );
  const renderTableHeaders = () => {
    if (width >= 1000) {
      return (
        <thead>
          <tr>
            {renderMinimumHeaders()}
            <th className="hand" onClick={sort('url')}>
              Url <FontAwesomeIcon icon="sort" />
            </th>
            <th>Cycle</th>
            <th>Collections</th>
          </tr>
        </thead>
      );
    } else {
      return (
        <thead>
          <tr>{renderMinimumHeaders()}</tr>
        </thead>
      );
    }
  };

  const renderTableRows = () => {
    if (width >= 1000) {
      return bookList.map((book, i) => (
        <tr key={`entity-${i}`} data-cy="entityTable">
          <td>
            <a href={`/book/${book.id}`} color="link">
              {book.title}
            </a>
          </td>
          <td>
            <table>
              <tbody>
                {book.authors?.map(item => (
                  <tr key={item.id}>
                    <td>
                      <Link to={`/author/${item.id}`}>{item.name}</Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </td>
          <td>{book.category}</td>
          <td>
            <a href={book.url} color="link">
              {book.url}
            </a>
          </td>
          <td>{book.cycle ? <Link to={`/cycle/${book.cycle.id}`}>{book.cycle.name}</Link> : ''}</td>
          <td>
            <table>
              <tbody>
                {book.collections?.map(item => (
                  <tr key={item.id}>
                    <td>
                      <Link to={`/collection/${item.id}`}>{item.name}</Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </td>
        </tr>
      ));
    } else {
      return bookList.map((book, i) => (
        <tr key={`entity-${i}`} data-cy="entityTable">
          <td>
            <a href={`/book/${book.id}`} color="link">
              {book.title}
            </a>
          </td>
          <td>
            <table>
              <tbody>
                {book.authors?.map(item => (
                  <tr key={item.id}>
                    <td>
                      <Link to={`/author/${item.id}`}>{item.name}</Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </td>
          <td>{book.category}</td>
        </tr>
      ));
    }
  };

  return (
    <div className="table-responsive">
      <Table responsive>
        {renderTableHeaders()}
        {bookList && bookList.length > 0 ? <tbody>{renderTableRows()}</tbody> : <tbody></tbody>}
      </Table>
    </div>
  );
};
export default BookTable;
