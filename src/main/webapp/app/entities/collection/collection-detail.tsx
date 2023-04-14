import React, { useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { Col, Row } from 'reactstrap';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './collection.reducer';
import GoToLegimiButton, { BookCard, DetailHeader, GoBack } from 'app/entities/ReusableComponents';

export const CollectionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const entity = useAppSelector(state => state.collection.entity);
  return (
    <Row>
      <Col>
        <DetailHeader id={entity.id} name={entity.name}></DetailHeader>
        <dl className="jh-entity-details">
          <dd className={'book-list'}>
            {entity.books &&
              [...entity.books].sort((a, b) => a.title.localeCompare(b.title)).map(item => <BookCard key={item.id} book={item} />)}
          </dd>
        </dl>
        <GoBack to={'/collection'} />
        {entity.url && <GoToLegimiButton href={entity.url} />}
      </Col>
    </Row>
  );
};

export default CollectionDetail;
