import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './cycle.reducer';

export const CycleDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const cycleEntity = useAppSelector(state => state.cycle.entity);
  console.log(cycleEntity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="cycleDetailsHeading">Cycle</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">Id</span>
          </dt>
          <dd>{cycleEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{cycleEntity.name}</dd>
          <dt>
            <span id="url">Url</span>
          </dt>
          <dd>
            <a href={cycleEntity.url}>{cycleEntity.url}</a>
          </dd>
          <dt>
            <span id="url">Books</span>
          </dt>
          {cycleEntity.books?.map(item => (
            <dd>
              <a href={`/book/${item.id}`} color="link">
                {item.title}
              </a>
            </dd>
          ))}
        </dl>
        <Button tag={Link} to="/cycle" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
      </Col>
    </Row>
  );
};

export default CycleDetail;
