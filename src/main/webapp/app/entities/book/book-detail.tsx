import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { ButtonGroup, Col, Row } from 'reactstrap';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './book.reducer';
import GoToLegimiButton, { DetailHeader, GoBack, RedirectToEntity, YesNoMark } from 'app/entities/ReusableComponents';

export const BookDetail = () => {
  const dispatch = useAppDispatch();
  const [imageError, setImageError] = useState(false);

  const handleImageError = () => {
    setImageError(true);
  };

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);
  const entity = useAppSelector(state => state.book.entity);
  const width = entity.category ? Math.max(entity.category.length * 10, 200) : 10;
  return (
    <Row>
      <DetailHeader id={entity.id} name={entity.title}></DetailHeader>
      <Col>
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
        </dl>
      </Col>
      <Col style={{ minWidth: width }}>
        {entity.authors && <dt>Authors</dt>}
        {entity.authors?.map(item => (
          <RedirectToEntity key={item.id} url={`/author/${item.id}`} name={item.name} />
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
        {entity.collections && entity.collections.length > 0 && <dt>Collections</dt>}
        {entity.collections?.map(item => (
          <RedirectToEntity key={item.id} url={`/collection/${item.id}`} name={item.name} />
        ))}
        <dd></dd>
      </Col>
      <Col>
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
      </Col>
      {entity.url && (
        <ButtonGroup>
          <GoBack to={'/book'} />
          <GoToLegimiButton href={entity.url} />
        </ButtonGroup>
      )}
    </Row>
  );
};

export default BookDetail;
