import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Table } from 'reactstrap';
import { getSortState, JhiItemCount, JhiPagination, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities } from './cycle.reducer';
import SyncLoader from 'react-spinners/SyncLoader';

export const Cycle = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getSortState(location, ITEMS_PER_PAGE, 'id'), location.search)
  );

  const cycleList = useAppSelector(state => state.cycle.entities);
  const loading = useAppSelector(state => state.cycle.loading);
  const totalItems = useAppSelector(state => state.cycle.totalItems);

  const [filterValue, setFilterValue] = useState(() => {
    const savedValue = window.localStorage.getItem('cycle-filter');
    return savedValue !== null ? savedValue : '';
  });

  const getAllEntities = () => {
    if (filterValue.length > 0) {
      dispatch(
        getEntities({
          page: paginationState.activePage - 1,
          size: paginationState.itemsPerPage,
          sort: `${paginationState.sort},${paginationState.order}`,
          query: filterValue,
        })
      );
    }
  };
  useEffect(() => {
    window.localStorage.setItem('cycle-filter', filterValue);
  }, [filterValue]);
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

  const handleFilter = () => {
    sortEntities();
  };

  return (
    <div>
      <h2 id="cycle-heading" data-cy="CycleHeading">
        Cycles
      </h2>
      <ValidatedForm onSubmit={handleFilter}>
        <ValidatedField type="text" name="name" placeholder="Filter" value={filterValue} onChange={e => setFilterValue(e.target.value)} />
      </ValidatedForm>
      {loading ? <SyncLoader></SyncLoader> : <div></div>}

      <div className="table-responsive">
        <Table responsive>
          <thead>
            <tr>
              <th className="hand">
                <div onClick={sort('name')}>
                  {' '}
                  Name <FontAwesomeIcon icon="sort" />
                </div>
              </th>
              <th className="hand" onClick={sort('url')}>
                Url <FontAwesomeIcon icon="sort" />
              </th>
              <th>Books</th>
            </tr>
          </thead>
          {cycleList && cycleList.length > 0 ? (
            <tbody>
              {cycleList.map((cycle, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <a href={`/cycle/${cycle.id}`} color="link">
                      {cycle.name}
                    </a>
                  </td>
                  <td>
                    <a href={cycle.url} color="link">
                      {cycle.url}
                    </a>
                  </td>
                  <td>{cycle.books.length}</td>
                </tr>
              ))}
            </tbody>
          ) : (
            !loading && <tbody></tbody>
          )}
        </Table>
      </div>
      {totalItems ? (
        <div className={cycleList && cycleList.length > 0 ? '' : 'd-none'}>
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

export default Cycle;
