import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Col, FormGroup, Label, Row, Table } from 'reactstrap';
import { getSortState, JhiItemCount, JhiPagination, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities, reload, reloadCollections, reloadCycles } from './book.reducer';
import SyncLoader from 'react-spinners/SyncLoader';
import { ButtonGroup } from '@mui/material';
import { loginAdmin } from 'app/modules/login/login';

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
  const [width, setWidth] = useState(window.innerWidth);

  const [filterTitle, setFilterTitle] = useState(() => {
    const savedValue = window.localStorage.getItem('filter.title');
    return savedValue !== null ? savedValue : '';
  });
  const [filterAuthor, setFilterAuthor] = useState(() => {
    const savedValue = window.localStorage.getItem('filter.author');
    return savedValue !== null ? savedValue : '';
  });
  const [filterCycle, setFilterCycle] = useState(() => {
    const savedValue = window.localStorage.getItem('filter.cycle');
    return savedValue !== null ? savedValue : '';
  });
  const [filterCollection, setFilterCollection] = useState(() => {
    const savedValue = window.localStorage.getItem('filter.collection');
    return savedValue !== null ? savedValue : '';
  });
  const [added, setAdded] = useState(() => {
    const savedValue = window.localStorage.getItem('filter.added');
    return savedValue !== null ? new Date(savedValue) : undefined;
  });

  useEffect(() => {
    window.localStorage.setItem('filter.title', filterTitle);
  }, [filterTitle]);
  useEffect(() => {
    window.localStorage.setItem('filter.cycle', filterCycle);
  }, [filterCycle]);
  useEffect(() => {
    window.localStorage.setItem('filter.collection', filterCycle);
  }, [filterCycle]);
  useEffect(() => {
    window.localStorage.setItem('filter.author', filterAuthor);
  }, [filterAuthor]);
  useEffect(() => {
    if (added !== undefined && added !== null) {
      window.localStorage.setItem('filter.added', added.toISOString());
      handleFilter();
    }
  }, [added]);

  const getAllEntities = () => {
    if (
      filterTitle.length > 0 ||
      filterAuthor.length > 0 ||
      formatAdded(added).length > 0 ||
      filterCycle.length > 0 ||
      filterCollection.length > 0
    ) {
      dispatch(
        getEntities({
          page: paginationState.activePage - 1,
          size: paginationState.itemsPerPage,
          sort: `${paginationState.sort},${paginationState.order}`,
          filterTitle: filterTitle,
          filterAuthor: filterAuthor,
          filterCycle: filterCycle,
          filterCollection: filterCollection,
          added: formatAdded(added),
        })
      );
    }
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (location.search !== endURL) {
      navigate(`${location.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort, added]);

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
  useEffect(() => {
    const handleResize = () => setWidth(window.innerWidth);
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);
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

  const handleReloadBooks = () => {
    dispatch(reload);
  };
  const handleReloadCycles = () => {
    dispatch(reloadCycles);
  };
  const handleReloadCollections = () => {
    dispatch(reloadCollections);
  };
  const handleFilter = () => {
    sortEntities();
  };
  const handleKeyDown = (e, callback) => {
    if (e.keyCode === 13) {
      e.preventDefault();
      callback();
    }
  };
  const formatAdded = date => {
    return date !== undefined && date !== null && typeof date === 'object'
      ? `${date.getDate()}/${date.getMonth() + 1}/${date.getFullYear()}`
      : '';
  };
  const handleClearClick = () => {
    window.localStorage.removeItem('filter.added');
    setAdded(undefined);
  };
  const handleClear = () => {
    window.localStorage.clear();
    setAdded(undefined);
    setFilterCollection('');
    setFilterCycle('');
    setFilterTitle('');
    setFilterAuthor('');
  };

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
    <div>
      <h2 id="book-heading" data-cy="BookHeading">
        <div className="d-flex justify-content-end">
          <Button onClick={handleReloadBooks} className="me-2" color="warning">
            <FontAwesomeIcon icon="right-left" />
            &nbsp; Crawl
          </Button>
          <Button onClick={handleReloadCycles} className="me-2" disabled color="warning">
            <FontAwesomeIcon icon="right-left" />
            &nbsp; Update cycles
          </Button>
          <Button onClick={handleReloadCollections} className="me-2" disabled color="warning">
            <FontAwesomeIcon icon="right-left" />
            &nbsp; Update collections
          </Button>
        </div>
      </h2>
      <ValidatedForm onSubmit={handleFilter}>
        <Row>
          <Col md="6">
            <ValidatedField
              type="text"
              name="title"
              label="Title"
              value={filterTitle}
              onChange={e => setFilterTitle(e.target.value)}
              onKeyDown={e => handleKeyDown(e, handleFilter)}
              onBlur={handleFilter}
            />
          </Col>
          <Col md="6">
            <ValidatedField
              type="text"
              name="author"
              label="Author"
              value={filterAuthor}
              onChange={e => setFilterAuthor(e.target.value)}
              onKeyDown={e => handleKeyDown(e, handleFilter)}
              onBlur={handleFilter}
            />
          </Col>
        </Row>
        <Row>
          <Col md="6">
            <ValidatedField
              type="text"
              name="collection"
              label="Collection"
              value={filterCollection}
              onChange={e => setFilterCollection(e.target.value)}
              onKeyDown={e => handleKeyDown(e, handleFilter)}
              onBlur={handleFilter}
            />
          </Col>
          <Col md="6">
            <ValidatedField
              type="text"
              name="cycle"
              label="Cycle"
              value={filterCycle}
              onChange={e => setFilterCycle(e.target.value)}
              onKeyDown={e => handleKeyDown(e, handleFilter)}
              onBlur={handleFilter}
            />
          </Col>
        </Row>
        <Row>
          <Col md="6">
            <FormGroup>
              <Label for="added">Added</Label>
              <DatePicker
                name="added"
                selected={added}
                onChange={date => setAdded(date)}
                isClearable={true}
                onClear={handleClearClick}
                dateFormat="dd/MM/yyyy" // specify date format
                label="Added"
                showYearDropdown
                scrollableYearDropdown // add these props to allow selecting year from dropdown
              />{' '}
            </FormGroup>
          </Col>
          <Col md="6">
            <ButtonGroup style={{ verticalAlign: 'bottom' }} className="btn-container">
              <Button onClick={handleFilter} className="me-2" color="success">
                <FontAwesomeIcon icon="search" />
                &nbsp; Search
              </Button>
              <Button onClick={handleClear} className="me-2" color="danger">
                <FontAwesomeIcon icon="trash" />
                &nbsp; Clear
              </Button>
            </ButtonGroup>
          </Col>
        </Row>
      </ValidatedForm>
      {loading ? <SyncLoader></SyncLoader> : <div></div>}

      <div className="table-responsive">
        <Table responsive>
          {renderTableHeaders()}
          {bookList && bookList.length > 0 ? <tbody>{renderTableRows()}</tbody> : !loading && <tbody></tbody>}
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
