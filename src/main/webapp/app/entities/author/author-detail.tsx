import React, { useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { Col, Row } from 'reactstrap';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './author.reducer';
import GoToLegimiButton, { BookCard, DetailHeader, GoBack } from 'app/entities/ReusableComponents';

export const AuthorDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const entity = useAppSelector(state => state.author.entity);

  return (
    <Row>
      <Col>
        <DetailHeader id={entity.id} name={entity.name}></DetailHeader>
        <dl className="jh-entity-details">
          <dd className={'book-list'}>
            {entity.books && [...entity.books].sort((a, b) => a.title.localeCompare(b.title)).map(item => <BookCard book={item} />)}
          </dd>
        </dl>
        <GoBack to={'/author'} />
        {entity.url && <GoToLegimiButton href={entity.url} />}
      </Col>
    </Row>
  );
};

export default AuthorDetail;
