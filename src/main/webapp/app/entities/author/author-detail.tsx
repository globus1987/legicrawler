import React, { useEffect } from 'react';
import { Link, useParams, useNavigate } from 'react-router-dom';
import { Row, Col, Button } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './author.reducer';

export const AuthorDetail = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const authorEntity = useAppSelector(state => state.author.entity);

  return (
    <Row>
      <Col md="8">
        <h2 data-cy="cycleDetailsHeading">
          {authorEntity.id} : {authorEntity.name}
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="url">Url</span>
          </dt>
          <dd>
            <a href={authorEntity.url}>{authorEntity.url}</a>
          </dd>
          <dt>
            <span id="url">Books</span>
          </dt>
          <dd className={'book-list'}>
            {authorEntity.books &&
              [...authorEntity.books]
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
        <Button tag={Link} to="/cycle" onClick={() => navigate(-1)} replace color="dark" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
      </Col>
    </Row>
  );
};

export default AuthorDetail;
