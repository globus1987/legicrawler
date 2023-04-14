import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { getSortState, JhiItemCount, JhiPagination } from 'react-jhipster';
import 'react-datepicker/dist/react-datepicker.css';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './book.reducer';
import SyncLoader from 'react-spinners/SyncLoader';
import BookTable from 'app/entities/book/bookTable';
import useLocalStorageState from './localStorage';
import { ActionButtons } from 'app/entities/ReusableComponents';
import BookFilter from 'app/entities/book/filter';

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
  interface Filters {
    [key: string]: [any, React.Dispatch<React.SetStateAction<any>>];
  }

  const filters: Filters = {
    title: [filterTitle, setFilterTitle],
    author: [filterAuthor, setFilterAuthor],
    cycle: [filterCycle, setFilterCycle],
    collection: [filterCollection, setFilterCollection],
    added: [added, setAdded],
  };

  const getAllEntities = () => {
    const requestFilters = {
      filterTitle,
      filterAuthor,
      filterCycle,
      filterCollection,
      added: formatAdded(added),
    };

    const hasFilters = Object.values(requestFilters).some(filter => filter.length > 0);

    if (hasFilters) {
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
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

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
  const formatAdded = date => {
    return date !== undefined && date !== null && typeof date === 'object'
      ? `${date.getDate()}/${date.getMonth() + 1}/${date.getFullYear()}`
      : '';
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
      <BookFilter filters={filters} handleFilter={handleFilter} handleClear={handleClear} />
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
