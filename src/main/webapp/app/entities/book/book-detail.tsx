import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './book.reducer';
import GoToLegimiButton, { GoBack, RedirectToEntity, YesNoMark } from 'app/entities/ReusableComponents';

export const BookDetail = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const [imageError, setImageError] = useState(false);

  const handleImageError = () => {
    setImageError(true);
  };

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const entity = useAppSelector(state => state.book.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="bookDetailsHeading">
          {entity.id} : {entity.title}
        </h2>
        <dl className="jh-entity-details">
          <dd>
            {!imageError && (
              <img
                style={{ overflow: 'hidden', borderRadius: '5px', boxShadow: '5px 5px 4px rgba(0, 0, 0, 0.1)' }}
                src={entity.imgsrc}
                alt="Book Cover"
                onError={handleImageError}
              />
            )}
          </dd>
          {entity.authors && <dt>Authors</dt>}
          {entity.authors?.map(item => (
            <RedirectToEntity url={`/author/${item.id}`} name={item.name} />
          ))}
          <dt>
            <span id="category">Category</span>
          </dt>
          <dd>{entity.category}</dd>
          {entity.cycle && (
            <dt>
              <span id="Cycle"> Cycle</span>
            </dt>
          )}
          {entity.cycle && <RedirectToEntity url={`/cycle/${entity.cycle.id}`} name={entity.cycle.name} />}
          {entity.collections && <dt>Collections</dt>}
          {entity.collections?.map(item => (
            <RedirectToEntity url={`/collection/${item.id}`} name={item.name} />
          ))}
          <dd></dd>
          <dt>
            <YesNoMark condition={entity.ebook}></YesNoMark>
            <span id="ebook">Ebook</span>
          </dt>

          <dt>
            <YesNoMark condition={entity.audiobook}></YesNoMark>
            <span id="audiobook">Audiobook</span>
          </dt>
          <dd></dd>

          <dt>
            <YesNoMark condition={entity.subscription}></YesNoMark>

            <span id="subscription"> Subscription</span>
          </dt>
          <dt>
            <YesNoMark condition={entity.kindleSubscription}></YesNoMark>
            <span id="kindleSubscription"> Kindle Subscription</span>
          </dt>
          <dt>
            <YesNoMark condition={entity.libraryPass}></YesNoMark>
            <span id="libraryPass"> Library Pass</span>
          </dt>
          <dt>
            <YesNoMark condition={entity.librarySubscription}></YesNoMark>
            <span id="librarySubscription"> Library Subscription</span>
          </dt>
        </dl>
        <GoBack to={'/book'} />
        <GoToLegimiButton href={entity.url} />
      </Col>
    </Row>
  );
};

export default BookDetail;
