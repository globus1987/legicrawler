import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './collection.reducer';

export const CollectionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const collectionEntity = useAppSelector(state => state.collection.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="collectionDetailsHeading">Collection</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">Id</span>
          </dt>
          <dd>{collectionEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{collectionEntity.name}</dd>
          <dt>
            <span id="url">Url</span>
          </dt>
          <dd>{collectionEntity.url}</dd>
          <dt>Books</dt>
          <dd>
            {collectionEntity.books
              ? collectionEntity.books.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {collectionEntity.books && i === collectionEntity.books.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/collection" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/collection/${collectionEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default CollectionDetail;
