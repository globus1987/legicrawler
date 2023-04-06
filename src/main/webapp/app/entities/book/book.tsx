import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate, TextFormat, getSortState, JhiPagination, JhiItemCount } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities, reload } from './book.reducer';

export const Book = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getSortState(location, ITEMS_PER_PAGE, 'id'), location.search)
  );

  const bookList = useAppSelector(state => state.book.entities);
  const loading = useAppSelector(state => state.book.loading);
  const totalItems = useAppSelector(state => state.book.totalItems);
  const [filterTitle, setFilterTitle] = useState('');
  const [filterAuthor, setFilterAuthor] = useState('');

  const getAllEntities = () => {
    // if (filterTitle.length > 0 || filterAuthor.length > 0) {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
        filterTitle: filterTitle,
        filterAuthor: filterAuthor,
      })
    );
    // }
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (location.search !== endURL) {
      navigate(`${location.pathname}${endURL}`);
    }
  };

  const doReload = () => {
    dispatch(reload);
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [location.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleReload = () => {
    doReload();
  };
  const handleFilter = () => {
    sortEntities();
  };
  return (
    <div>
      <h2 id="book-heading" data-cy="BookHeading">
        Books
        <div className="d-flex justify-content-end">
          <Button onClick={handleReload} className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="right-left" />
            &nbsp; Reload
          </Button>
        </div>
      </h2>
      <ValidatedForm onSubmit={handleFilter}>
        <ValidatedField type="text" name="title" label="Title" value={filterTitle} onChange={e => setFilterTitle(e.target.value)} />
        <ValidatedField type="text" name="author" label="Author" value={filterAuthor} onChange={e => setFilterAuthor(e.target.value)} />
      </ValidatedForm>
      <Button className="me-2" color="info" onClick={handleFilter} disabled={loading}>
        <FontAwesomeIcon icon="search" spin={loading} /> Search
      </Button>
      <div className="table-responsive">
        <Table responsive>
          <thead>
            <tr>
              <th className="hand" onClick={sort('title')}>
                Title <FontAwesomeIcon icon="sort" />
              </th>
              <th className="hand">Authors</th>
              <th className="hand" onClick={sort('url')}>
                Url <FontAwesomeIcon icon="sort" />
              </th>
              <th className="hand" onClick={sort('category')}>
                Category <FontAwesomeIcon icon="sort" />
              </th>
              <th>Cycle</th>
              <th>Ebook</th>
              <th>Audiobook</th>
              <th className="hand" onClick={sort('added')}>
                Added <FontAwesomeIcon icon="sort" />
              </th>
              <th className="hand">Subscription</th>
            </tr>
          </thead>
          {bookList && bookList.length > 0 ? (
            <tbody>
              {bookList.map((book, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <a href={`/book/${book.id}`} color="link">
                      {book.title}
                    </a>
                  </td>
                  <td>
                    {book.authors?.map(item => (
                      <a href={`/author/${item.id}`} color="link">
                        {item.name}
                      </a>
                    ))}
                  </td>
                  <td>
                    <a href={book.url} color="link">
                      {book.url}
                    </a>
                  </td>
                  <td>{book.category}</td>
                  <td>{book.cycle ? <Link to={`/cycle/${book.cycle.id}`}>{book.cycle.name}</Link> : ''}</td>
                  <td>
                    {<FontAwesomeIcon style={book.ebook ? { color: 'green' } : { color: 'red' }} icon={book.ebook ? 'check' : 'xmark'} />}
                  </td>
                  <td>
                    <FontAwesomeIcon
                      style={book.audiobook ? { color: 'green' } : { color: 'red' }}
                      icon={book.audiobook ? 'check' : 'xmark'}
                    />
                  </td>
                  <td>{book.added ? <TextFormat type="date" value={book.added} format={APP_LOCAL_DATE_FORMAT} /> : null}</td>
                  <td>
                    <p>
                      <FontAwesomeIcon
                        style={book.kindleSubscription ? { color: 'green' } : { color: 'red' }}
                        icon={book.kindleSubscription ? 'check' : 'xmark'}
                      />
                      Kindle
                    </p>
                    <p>
                      <FontAwesomeIcon
                        style={book.libraryPass ? { color: 'green' } : { color: 'red' }}
                        icon={book.libraryPass ? 'check' : 'xmark'}
                      />
                      Library Pass
                    </p>
                    <p>
                      <FontAwesomeIcon
                        style={book.librarySubscription ? { color: 'green' } : { color: 'red' }}
                        icon={book.librarySubscription ? 'check' : 'xmark'}
                      />
                      Library Subscription
                    </p>
                    <p>
                      <FontAwesomeIcon
                        style={book.subscription ? { color: 'green' } : { color: 'red' }}
                        icon={book.subscription ? 'check' : 'xmark'}
                      />
                      Subscription
                    </p>
                  </td>
                </tr>
              ))}
            </tbody>
          ) : (
            !loading && <tbody></tbody>
          )}
        </Table>
      </div>
      {totalItems ? (
        <div className={bookList && bookList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} />
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

export default Book;
