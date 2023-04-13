import React, { useEffect } from 'react';
import { Link, useParams, useNavigate } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './cycle.reducer';

export const CycleDetail = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  let cycleEntity = useAppSelector(state => state.cycle.entity);

  return (
    <Row>
      <Col md="8">
        <h2 data-cy="cycleDetailsHeading">
          {cycleEntity.id} : {cycleEntity.name}
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="url">Url</span>
          </dt>
          <dd>
            <a href={cycleEntity.url}>{cycleEntity.url}</a>
          </dd>
          <dt>
            <span id="url">Books</span>
          </dt>
          <dd className={'book-list'}>
            {cycleEntity.books &&
              [...cycleEntity.books]
                .sort((a, b) => a.id - b.id)
                .map(item => (
                  <dd key={item.id}>
                    <Button onClick={() => navigate(`/book/${item.id}`)} color="light">
                      {item.title}
                    </Button>
                  </dd>
                ))}
          </dd>
        </dl>
        <Button tag={Link} to="/cycle" onClick={() => navigate(-1)} replace color="dark" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
      </Col>
    </Row>
  );
};

export default CycleDetail;
