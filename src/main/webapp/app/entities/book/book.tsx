import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Button, Col, FormGroup, Label, Row } from 'reactstrap';
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
import BookTable from 'app/entities/book/bookTable';
import useLocalStorageState from './localStorage';
import { ActionButtons } from 'app/entities/ReusableComponents';

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

  const [filterTitle, setFilterTitle] = useLocalStorageState('filter.title', '');
  const [filterAuthor, setFilterAuthor] = useLocalStorageState('filter.author', '');
  const [filterCycle, setFilterCycle] = useLocalStorageState('filter.cycle', '');
  const [filterCollection, setFilterCollection] = useLocalStorageState('filter.collection', '');
  const [added, setAdded] = useLocalStorageState('filter.added', undefined);

  const getAllEntities = () => {
    const filters = {
      filterTitle,
      filterAuthor,
      filterCycle,
      filterCollection,
      added: formatAdded(added),
    };

    const hasFilters = Object.values(filters).some(filter => filter.length > 0);

    if (hasFilters) {
      dispatch(
        getEntities({
          page: paginationState.activePage - 1,
          size: paginationState.itemsPerPage,
          sort: `${paginationState.sort},${paginationState.order}`,
          ...filters,
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
    setPaginationState(prevState => ({
      ...prevState,
      activePage: +page ?? prevState.activePage,
      sort: sort?.split(',')[0] ?? prevState.sort,
      order: sort?.split(',')[1] ?? prevState.order,
    }));
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

  return (
    <div>
      <h2 id="book-heading" data-cy="BookHeading">
        <ActionButtons />
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

      <BookTable bookList={bookList} sort={sort} />
      {totalItems ? (
        <div className={bookList && bookList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} />
          </div>
          <div className="justify-content-center d-flex pagination">
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
