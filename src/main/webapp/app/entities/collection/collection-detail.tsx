import React, { useEffect } from 'react';
import { Link, useParams, useNavigate } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './collection.reducer';

export const CollectionDetail = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

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
          <dd>
            <a href={collectionEntity.url} color="link">
              {collectionEntity.url}
            </a>{' '}
          </dd>
          <dt>Books</dt>
          <dd className={'book-list'}>
            {collectionEntity.books &&
              [...collectionEntity.books]
                .sort((a, b) => a.title.localeCompare(b.title))
                .map(item => (
                  <dd key={item.id}>
                    <Button onClick={() => navigate(`/book/${item.id}`)} color="light">
                      {item.title}
                    </Button>
                  </dd>
                ))}
          </dd>
        </dl>
        <Button tag={Link} to="/collection" onClick={() => navigate(-1)} replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
      </Col>
    </Row>
  );
};

export default CollectionDetail;
